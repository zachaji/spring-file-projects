import { Component } from '@angular/core';
import { FileDownload } from './services/file-download';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'Opplan File Download Client';
  isDownloading = false;
  downloadSuccess = false;
  errorMessage = '';

  constructor(private fileDownloadService: FileDownload) {}

  downloadFile(): void {
    this.isDownloading = true;
    this.downloadSuccess = false;
    this.errorMessage = '';

    this.fileDownloadService.downloadFile().subscribe({
      next: (blob: Blob) => {
        this.fileDownloadService.saveFile(blob, 'Thumbnail-AWS.jpg');
        this.isDownloading = false;
        this.downloadSuccess = true;
        console.log('File downloaded successfully');
      },
      error: (error) => {
        this.isDownloading = false;
        this.errorMessage = 'Failed to download file. Please try again.';
        console.error('Error downloading file:', error);
      }
    });
  }
}
