<?php

namespace App\Http\Controllers;

use App\Models\Archive;
use App\Models\ArchiveFile;
use App\Models\FileVersion;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use ZipArchive;

class ArchiveController extends Controller
{
    /**
     * 获取归档列表
     */
    public function index(Request $request)
    {
        $query = Archive::query();

        if ($request->has('search')) {
            $search = $request->input('search');
            $query->where('name', 'like', "%{$search}%")
                ->orWhere('description', 'like', "%{$search}%");
        }

        $archives = $query->latest()->paginate(10);

        return response()->json([
            'data' => $archives
        ]);
    }

    /**
     * 获取单个归档
     */
    public function show(Archive $archive)
    {
        $archive->load('files');
        return response()->json([
            'data' => $archive
        ]);
    }

    /**
     * 创建归档
     */
    public function store(Request $request)
    {
        $request->validate([
            'name' => 'required|string|max:255',
            'description' => 'nullable|string',
            'files' => 'required|array',
            'files.*' => 'required|file|max:10240' // 最大10MB
        ]);

        $archive = Archive::create([
            'name' => $request->input('name'),
            'description' => $request->input('description'),
            'status' => 'processing'
        ]);

        $totalSize = 0;
        $fileCount = 0;

        foreach ($request->file('files') as $file) {
            $path = $file->store('archives/' . $archive->id);
            $totalSize += $file->getSize();
            $fileCount++;

            $archiveFile = ArchiveFile::create([
                'archive_id' => $archive->id,
                'name' => $file->getClientOriginalName(),
                'path' => $path,
                'size' => $file->getSize(),
                'type' => $file->getMimeType(),
                'version' => 1
            ]);

            // 创建初始版本
            FileVersion::create([
                'file_id' => $archiveFile->id,
                'number' => 1,
                'path' => $path,
                'size' => $file->getSize(),
                'description' => '初始版本',
                'is_current' => true
            ]);
        }

        $archive->update([
            'size' => $totalSize,
            'file_count' => $fileCount,
            'status' => 'completed'
        ]);

        return response()->json([
            'data' => $archive
        ], 201);
    }

    /**
     * 删除归档
     */
    public function destroy(Archive $archive)
    {
        // 删除所有文件
        foreach ($archive->files as $file) {
            foreach ($file->versions as $version) {
                Storage::delete($version->path);
            }
            Storage::delete($file->path);
        }

        // 删除归档目录
        Storage::deleteDirectory('archives/' . $archive->id);

        $archive->delete();

        return response()->json(null, 204);
    }

    /**
     * 下载归档
     */
    public function download(Archive $archive)
    {
        $zip = new ZipArchive();
        $zipPath = storage_path('app/temp/' . Str::uuid() . '.zip');
        $zip->open($zipPath, ZipArchive::CREATE | ZipArchive::OVERWRITE);

        foreach ($archive->files as $file) {
            $currentVersion = $file->currentVersion();
            if ($currentVersion) {
                $zip->addFile(
                    storage_path('app/' . $currentVersion->path),
                    $file->name
                );
            }
        }

        $zip->close();

        return response()->download($zipPath)->deleteFileAfterSend();
    }
} 