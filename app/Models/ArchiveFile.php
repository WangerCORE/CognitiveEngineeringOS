<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class ArchiveFile extends Model
{
    use HasFactory;

    protected $fillable = [
        'archive_id',
        'name',
        'path',
        'size',
        'type',
        'version'
    ];

    protected $casts = [
        'size' => 'integer',
        'version' => 'integer'
    ];

    /**
     * 获取文件所属的归档
     */
    public function archive(): BelongsTo
    {
        return $this->belongsTo(Archive::class);
    }

    /**
     * 获取文件的所有版本
     */
    public function versions(): HasMany
    {
        return $this->hasMany(FileVersion::class);
    }

    /**
     * 获取当前版本
     */
    public function currentVersion()
    {
        return $this->versions()->latest()->first();
    }
} 