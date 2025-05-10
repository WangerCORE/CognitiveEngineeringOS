package com.ceos.fetch.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.retry.support.RetryTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetryServiceTest {

    @Mock
    private RetryTemplate retryTemplate;

    private RetryService retryService;

    @BeforeEach
    void setUp() {
        retryService = new RetryService(retryTemplate);
    }

    @Test
    void executeWithRetry_WhenSuccess_ShouldReturnResult() throws Exception {
        // Arrange
        String expectedResult = "success";
        when(retryTemplate.execute(any())).thenReturn(expectedResult);

        // Act
        String result = retryService.executeWithRetry("test-operation", () -> expectedResult);

        // Assert
        assertEquals(expectedResult, result);
        verify(retryTemplate).execute(any());
    }

    @Test
    void executeWithRetry_WhenFailure_ShouldPropagateException() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(retryTemplate.execute(any())).thenThrow(expectedException);

        // Act & Assert
        RuntimeException actualException = assertThrows(RuntimeException.class, () ->
            retryService.executeWithRetry("test-operation", () -> {
                throw expectedException;
            })
        );

        assertEquals(expectedException, actualException);
        verify(retryTemplate).execute(any());
    }

    @Test
    void executeWithRetry_WithRetryCallback_ShouldExecuteCallback() throws Exception {
        // Arrange
        String expectedResult = "callback result";
        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            RetryService.RetryCallback<String> callback = invocation.getArgument(0);
            return callback.execute();
        });

        // Act
        String result = retryService.executeWithRetry("test-operation", () -> expectedResult);

        // Assert
        assertEquals(expectedResult, result);
        verify(retryTemplate).execute(any());
    }
} 