import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FileDownload {
  private apiUrl = 'http://localhost:8080/api/files/download';

  constructor(private http: HttpClient) {}

  downloadFile(): Observable<Blob> {
    return this.http.get(this.apiUrl, {
      responseType: 'blob',
      observe: 'body'
    });
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
