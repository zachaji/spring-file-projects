import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileDownload {
  private apiUrl = 'http://localhost:8080/api/files/download';
  private apiBytesUrl = 'http://localhost:8080/api/files/download-bytes';
  private apiResourceUrl = 'http://localhost:8080/api/files/download-resource';
  private apiByteArrayResourceUrl = 'http://localhost:8080/api/files/download-byte-array-resource';

  constructor(private http: HttpClient) {}

  downloadFile(): Observable<HttpResponse<Blob>> {
    return this.http.get(this.apiUrl, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  downloadFileAsBytes(): Observable<HttpResponse<Blob>> {
    return this.http.get(this.apiBytesUrl, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  downloadFileAsResource(): Observable<HttpResponse<Blob>> {
    return this.http.get(this.apiResourceUrl, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  downloadFileAsByteArrayResource(): Observable<HttpResponse<Blob>> {
    return this.http.get(this.apiByteArrayResourceUrl, {
      responseType: 'blob',
      observe: 'response'
    });
  }

  extractFilename(response: HttpResponse<Blob>): string {
    const contentDisposition = response.headers.get('Content-Disposition');
    if (contentDisposition) {
      const match = contentDisposition.match(/filename="?([^";\s]+)"?/);
      if (match) {
        return match[1];
      }
    }
    return 'download';
  }

  saveFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}
