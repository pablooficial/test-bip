import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio, BeneficioCreateDTO, BeneficioUpdateDTO, TransferenciaDTO } from '../models/beneficio.interface';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private apiUrl = 'http://localhost:8080/api/v1/beneficios';

  constructor(private http: HttpClient) { }

  findAll(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.apiUrl);
  }

  findById(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.apiUrl}/${id}`);
  }

  findAtivos(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(`${this.apiUrl}/ativos`);
  }

  findByNome(nome: string): Observable<Beneficio[]> {
    const params = new HttpParams().set('nome', nome);
    return this.http.get<Beneficio[]>(`${this.apiUrl}/buscar`, { params });
  }

  create(dto: BeneficioCreateDTO): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.apiUrl, dto);
  }

  update(id: number, dto: BeneficioUpdateDTO): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.apiUrl}/${id}`, dto);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  transfer(dto: TransferenciaDTO): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/transferir`, dto);
  }
}
