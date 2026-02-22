import { Component } from '@angular/core';
import { FileDownload } from './services/file-download';
import { CommonModule } from '@angular/common';
import { HttpResponse } from '@angular/common/http';

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

  // Resource download state
  isResourceDownloading = false;
  resourceDownloadSuccess = false;
  resourceErrorMessage = '';

  // ByteArrayResource download state
  isByteArrayResourceDownloading = false;
  byteArrayResourceDownloadSuccess = false;
  byteArrayResourceErrorMessage = '';

  constructor(private fileDownloadService: FileDownload) {}

  private handleDownload(response: HttpResponse<Blob>): void {
    const blob = response.body!;
    const filename = this.fileDownloadService.extractFilename(response);
    this.fileDownloadService.saveFile(blob, filename);
  }

  downloadFile(): void {
    this.isDownloading = true;
    this.downloadSuccess = false;
    this.errorMessage = '';

    this.fileDownloadService.downloadFile().subscribe({
      next: (response) => {
        this.handleDownload(response);
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
      next: (response) => {
        this.handleDownload(response);
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

  downloadFileAsResource(): void {
    this.isResourceDownloading = true;
    this.resourceDownloadSuccess = false;
    this.resourceErrorMessage = '';

    this.fileDownloadService.downloadFileAsResource().subscribe({
      next: (response) => {
        this.handleDownload(response);
        this.isResourceDownloading = false;
        this.resourceDownloadSuccess = true;
        console.log('File downloaded successfully (Resource)');
      },
      error: (error) => {
        this.isResourceDownloading = false;
        this.resourceErrorMessage = 'Failed to download file. Please try again.';
        console.error('Error downloading file (Resource):', error);
      }
    });
  }

  downloadFileAsByteArrayResource(): void {
    this.isByteArrayResourceDownloading = true;
    this.byteArrayResourceDownloadSuccess = false;
    this.byteArrayResourceErrorMessage = '';

    this.fileDownloadService.downloadFileAsByteArrayResource().subscribe({
      next: (response) => {
        this.handleDownload(response);
        this.isByteArrayResourceDownloading = false;
        this.byteArrayResourceDownloadSuccess = true;
        console.log('File downloaded successfully (ByteArrayResource)');
      },
      error: (error) => {
        this.isByteArrayResourceDownloading = false;
        this.byteArrayResourceErrorMessage = 'Failed to download file. Please try again.';
        console.error('Error downloading file (ByteArrayResource):', error);
      }
    });
  }
}
