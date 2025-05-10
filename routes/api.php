// 文件归档路由
Route::prefix('archives')->group(function () {
    Route::get('/', [ArchiveController::class, 'index']);
    Route::post('/', [ArchiveController::class, 'store']);
    Route::get('/{archive}', [ArchiveController::class, 'show']);
    Route::delete('/{archive}', [ArchiveController::class, 'destroy']);
    Route::get('/{archive}/download', [ArchiveController::class, 'download']);

    // 文件版本路由
    Route::get('/files/{file}/versions', [FileVersionController::class, 'index']);
    Route::post('/files/{file}/versions', [FileVersionController::class, 'store']);
    Route::get('/versions/{version}/download', [FileVersionController::class, 'download']);
    Route::post('/versions/{version}/restore', [FileVersionController::class, 'restore']);
}); 