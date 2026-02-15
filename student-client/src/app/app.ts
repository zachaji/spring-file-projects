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
  protected title = 'Student File Download Client';

  // Streaming download state
  isDownloading = false;
  downloadSuccess = false;
  errorMessage = '';

  // Byte[] download state
  isBytesDownloading = false;
  bytesDownloadSuccess = false;
  bytesErrorMessage = '';

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
        console.log('File downloaded successfully (streaming)');
      },
      error: (error) => {
        this.isDownloading = false;
        this.errorMessage = 'Failed to download file. Please try again.';
        console.error('Error downloading file (streaming):', error);
      }
    });
  }

  downloadFileAsBytes(): void {
    this.isBytesDownloading = true;
    this.bytesDownloadSuccess = false;
    this.bytesErrorMessage = '';

    this.fileDownloadService.downloadFileAsBytes().subscribe({
      next: (blob: Blob) => {
        this.fileDownloadService.saveFile(blob, 'Thumbnail-AWS.jpg');
        this.isBytesDownloading = false;
        this.bytesDownloadSuccess = true;
        console.log('File downloaded successfully (byte[])');
      },
      error: (error) => {
        this.isBytesDownloading = false;
        this.bytesErrorMessage = 'Failed to download file. Please try again.';
        console.error('Error downloading file (byte[]):', error);
      }
    });
  }
}
