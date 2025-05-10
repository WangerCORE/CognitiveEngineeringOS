<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class FileVersion extends Model
{
    use HasFactory;

    protected $fillable = [
        'file_id',
        'number',
        'path',
        'size',
        'description',
        'is_current'
    ];

    protected $casts = [
        'number' => 'integer',
        'size' => 'integer',
        'is_current' => 'boolean'
    ];

    /**
     * 获取版本所属的文件
     */
    public function file(): BelongsTo
    {
        return $this->belongsTo(ArchiveFile::class, 'file_id');
    }
} 