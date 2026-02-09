import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.interface';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-beneficios-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="page-header fade-in">
      <div>
        <h1>Benefícios</h1>
        <p class="subtitle">Gerencie os benefícios ativos e realize transferências</p>
      </div>
      <button class="btn btn-primary" [routerLink]="['/beneficios/novo']">
        <span>+</span> Novo Benefício
      </button>
    </div>

    <div class="search-bar glass-panel fade-in" style="animation-delay: 0.1s">
      <div class="search-input">
        <span class="icon">🔍</span>
        <input type="text" [(ngModel)]="searchTerm" (input)="search()" placeholder="Buscar por nome...">
      </div>
    </div>

    <div class="table-container glass-panel fade-in" style="animation-delay: 0.2s">
      <table class="modern-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Descrição</th>
            <th>Valor</th>
            <th>Status</th>
            <th class="actions">Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let b of beneficios">
            <td class="id-cell">#{{b.id}}</td>
            <td class="name-cell">{{b.nome}}</td>
            <td class="desc-cell">{{b.descricao || '-'}}</td>
            <td class="amount-cell">{{b.valor | currency:'BRL'}}</td>
            <td>
              <span class="badge" [class.active]="b.ativo" [class.inactive]="!b.ativo">
                {{b.ativo ? 'Ativo' : 'Inativo'}}
              </span>
            </td>
            <td class="actions">
              <button class="icon-btn edit" [routerLink]="['/beneficios/editar', b.id]">✏️</button>
              <button class="icon-btn delete" (click)="deleteBeneficio(b)">🗑️</button>
            </td>
          </tr>
          <tr *ngIf="beneficios.length === 0">
            <td colspan="6" class="empty-state">
              <div class="empty-icon">📭</div>
              <p>Nenhum benefício encontrado.</p>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    h1 { font-size: 2.5rem; }
    .subtitle { color: var(--text-muted); }

    .search-bar {
      padding: 1rem;
      margin-bottom: 2rem;
    }

    .search-input {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      background: rgba(0, 0, 0, 0.2);
      padding: 0.5rem 1rem;
      border-radius: 0.5rem;
      border: 1px solid var(--border-color);
    }

    .search-input input {
      background: transparent;
      border: none;
      color: white;
      width: 100%;
      outline: none;
      font-size: 1rem;
    }

    .table-container {
      overflow-x: auto;
    }

    .modern-table {
      width: 100%;
      border-collapse: collapse;
      text-align: left;
    }

    .modern-table th {
      padding: 1.25rem 1rem;
      color: var(--text-muted);
      font-weight: 600;
      text-transform: uppercase;
      font-size: 0.75rem;
      letter-spacing: 0.05em;
      border-bottom: 1px solid var(--border-color);
    }

    .modern-table td {
      padding: 1.25rem 1rem;
      border-bottom: 1px solid var(--border-color);
    }

    .id-cell { color: var(--text-muted); font-family: monospace; }
    .name-cell { font-weight: 600; }
    .amount-cell { font-weight: 700; color: var(--success); }

    .badge {
      padding: 0.25rem 0.75rem;
      border-radius: 999px;
      font-size: 0.75rem;
      font-weight: 600;
    }

    .badge.active { background: rgba(34, 197, 94, 0.2); color: #4ade80; }
    .badge.inactive { background: rgba(239, 68, 68, 0.2); color: #f87171; }

    .actions {
      display: flex;
      gap: 0.5rem;
      justify-content: flex-end;
    }

    .icon-btn {
      background: transparent;
      border: 1px solid var(--border-color);
      padding: 0.4rem;
      border-radius: 0.4rem;
      cursor: pointer;
      transition: all 0.2s;
    }

    .icon-btn:hover { background: rgba(255, 255, 255, 0.1); }
    .icon-btn.delete:hover { border-color: var(--danger); background: rgba(239, 68, 68, 0.1); }

    .empty-state {
      text-align: center;
      padding: 4rem !important;
      color: var(--text-muted);
    }

    .empty-icon { font-size: 3rem; margin-bottom: 1rem; }
  `]
})
export class BeneficiosListComponent implements OnInit {
  beneficios: Beneficio[] = [];
  searchTerm: string = '';

  constructor(private service: BeneficioService) {}

  ngOnInit() {
    this.loadBeneficios();
  }

  loadBeneficios() {
    this.service.findAll().subscribe(data => {
      this.beneficios = data;
    });
  }

  search() {
    if (this.searchTerm.trim()) {
      this.service.findByNome(this.searchTerm).subscribe(data => {
        this.beneficios = data;
      });
    } else {
      this.loadBeneficios();
    }
  }

  deleteBeneficio(b: Beneficio) {
    if (confirm(`Tem certeza que deseja inativar o benefício "${b.nome}"?`)) {
      this.service.delete(b.id!).subscribe(() => {
        this.loadBeneficios();
      });
    }
  }
}
