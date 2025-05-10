<?php

namespace App\Http\Controllers;

use App\Models\ArchiveFile;
use App\Models\FileVersion;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;

class FileVersionController extends Controller
{
    /**
     * 获取文件版本历史
     */
    public function index(ArchiveFile $file)
    {
        $versions = $file->versions()->orderBy('number', 'desc')->get();
        return response()->json([
            'data' => [
                'id' => $file->id,
                'name' => $file->name,
                'versions' => $versions
            ]
        ]);
    }

    /**
     * 创建新版本
     */
    public function store(Request $request, ArchiveFile $file)
    {
        $request->validate([
            'file' => 'required|file|max:10240', // 最大10MB
            'description' => 'nullable|string'
        ]);

        // 获取当前版本号
        $currentVersion = $file->currentVersion();
        $newVersionNumber = $currentVersion ? $currentVersion->number + 1 : 1;

        // 存储新版本文件
        $path = $request->file('file')->store('archives/' . $file->archive_id);

        // 创建新版本记录
        $version = FileVersion::create([
            'file_id' => $file->id,
            'number' => $newVersionNumber,
            'path' => $path,
            'size' => $request->file('file')->getSize(),
            'description' => $request->input('description'),
            'is_current' => true
        ]);

        // 更新旧版本状态
        if ($currentVersion) {
            $currentVersion->update(['is_current' => false]);
        }

        // 更新文件信息
        $file->update([
            'version' => $newVersionNumber,
            'size' => $request->file('file')->getSize()
        ]);

        return response()->json([
            'data' => $version
        ], 201);
    }

    /**
     * 下载特定版本
     */
    public function download(FileVersion $version)
    {
        return response()->download(
            storage_path('app/' . $version->path),
            $version->file->name
        );
    }

    /**
     * 恢复版本
     */
    public function restore(FileVersion $version)
    {
        // 更新当前版本状态
        $version->file->versions()->update(['is_current' => false]);
        $version->update(['is_current' => true]);

        // 更新文件信息
        $version->file->update([
            'version' => $version->number,
            'size' => $version->size
        ]);

        return response()->json([
            'data' => $version
        ]);
    }
} 