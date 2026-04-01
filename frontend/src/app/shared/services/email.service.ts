import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { Email } from '../models/email.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class EmailService {
  readonly #apiUrl = `${environment.apiUrl}/mails`;
  readonly #http = inject(HttpClient);

  sendEmail(email: Email): Observable<void> {
    const url = this.#apiUrl + '/receive';
    return this.#http.post<void>(url, email);
  }

  sendEmailWithAttachment(email: Email): Observable<void> {
    const url = this.#apiUrl + '/receiveWithAttachment';
    return this.#http.post<void>(url, email);
  }
}
