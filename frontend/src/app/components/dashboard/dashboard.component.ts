import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.interface';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-header fade-in">
      <h1>Bem-vindo ao BenefitX</h1>
      <p class="subtitle">Visão geral do sistema de gestão de benefícios</p>
    </div>

    <div class="stats-grid fade-in" style="animation-delay: 0.1s">
      <div class="stat-card glass-panel">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-label">Total em Benefícios</div>
          <div class="stat-value">{{totalValor | currency:'BRL'}}</div>
        </div>
      </div>

      <div class="stat-card glass-panel">
        <div class="stat-icon">📋</div>
        <div class="stat-info">
          <div class="stat-label">Benefícios Ativos</div>
          <div class="stat-value">{{totalAtivos}}</div>
        </div>
      </div>

      <div class="stat-card glass-panel">
        <div class="stat-icon">📊</div>
        <div class="stat-info">
          <div class="stat-label">Média por Benefício</div>
          <div class="stat-value">{{mediaValor | currency:'BRL'}}</div>
        </div>
      </div>
    </div>

    <div class="quick-actions fade-in" style="animation-delay: 0.2s">
      <h2>Ações Rápidas</h2>
      <div class="actions-grid">
        <div class="action-card glass-panel" routerLink="/beneficios/novo">
          <div class="action-icon">+</div>
          <div class="action-text">Criar Novo Benefício</div>
        </div>
        <div class="action-card glass-panel" routerLink="/transferir">
          <div class="action-icon">⇄</div>
          <div class="action-text">Realizar Transferência</div>
        </div>
        <div class="action-card glass-panel" routerLink="/beneficios">
          <div class="action-icon">🔎</div>
          <div class="action-text">Consultar Listagem</div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-header { margin-bottom: 3rem; }
    h1 { font-size: 3rem; margin-bottom: 0.5rem; }
    .subtitle { color: var(--text-muted); font-size: 1.1rem; }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 2rem;
      margin-bottom: 4rem;
    }

    .stat-card {
      padding: 2rem;
      display: flex;
      align-items: center;
      gap: 1.5rem;
      transition: transform 0.3s;
    }

    .stat-card:hover { transform: translateY(-5px); }

    .stat-icon {
      font-size: 2.5rem;
      background: rgba(255,255,255,0.05);
      width: 70px;
      height: 70px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 1rem;
    }

    .stat-label { color: var(--text-muted); font-size: 0.875rem; margin-bottom: 0.25rem; }
    .stat-value { font-size: 1.75rem; font-weight: 700; color: var(--primary-color); }

    .quick-actions h2 { margin-bottom: 2rem; color: var(--text-muted); font-weight: 600; font-size: 1rem; text-transform: uppercase; letter-spacing: 0.1em; }

    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
    }

    .action-card {
      padding: 1.5rem;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;
    }

    .action-card:hover {
      border-color: var(--primary-color);
      background: rgba(99, 102, 241, 0.1);
      transform: scale(1.02);
    }

    .action-icon {
      font-size: 2rem;
      margin-bottom: 1rem;
      color: var(--secondary-color);
    }

    .action-text { font-weight: 600; }
  `]
})
export class DashboardComponent implements OnInit {
  totalValor = 0;
  totalAtivos = 0;
  mediaValor = 0;

  constructor(private service: BeneficioService) {}

  ngOnInit() {
    this.service.findAll().subscribe(data => {
      const ativos = data.filter(b => b.ativo);
      this.totalAtivos = ativos.length;
      this.totalValor = ativos.reduce((acc, curr) => acc + curr.valor, 0);
      this.mediaValor = this.totalAtivos > 0 ? this.totalValor / this.totalAtivos : 0;
    });
  }
}
