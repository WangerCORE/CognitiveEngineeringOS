<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Archive extends Model
{
    use HasFactory;

    protected $fillable = [
        'name',
        'description',
        'status',
        'size',
        'file_count'
    ];

    protected $casts = [
        'size' => 'integer',
        'file_count' => 'integer'
    ];

    /**
     * 获取归档中的所有文件
     */
    public function files(): HasMany
    {
        return $this->hasMany(ArchiveFile::class);
    }
} 