import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio, TransferenciaDTO } from '../../models/beneficio.interface';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-transferencia',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header fade-in">
      <h1>Transferência de Saldo</h1>
      <p class="subtitle">Mova valores entre benefícios ativos de forma segura</p>
    </div>

    <div class="transfer-card glass-panel fade-in" style="animation-delay: 0.1s">
      <form [formGroup]="transferForm" (ngSubmit)="onSubmit()">
        <div class="transfer-flow">
          <!-- Origem -->
          <div class="flow-step">
            <div class="step-label">DE</div>
            <div class="form-group">
              <label>Benefício de Origem</label>
              <select formControlName="fromId" (change)="onSourceChange()">
                <option [ngValue]="null" disabled>Selecione a origem</option>
                <option *ngFor="let b of ativos" [ngValue]="b.id">
                  {{b.nome}} ({{b.valor | currency:'BRL'}})
                </option>
              </select>
            </div>
          </div>

          <!-- Ícone de Fluxo -->
          <div class="flow-arrow">
            <div class="arrow-icon">➔</div>
          </div>

          <!-- Destino -->
          <div class="flow-step">
            <div class="step-label">PARA</div>
            <div class="form-group">
              <label>Benefício de Destino</label>
              <select formControlName="toId">
                <option [ngValue]="null" disabled>Selecione o destino</option>
                <option *ngFor="let b of ativos" [ngValue]="b.id" [disabled]="b.id === transferForm.value.fromId">
                  {{b.nome}} ({{b.valor | currency:'BRL'}})
                </option>
              </select>
            </div>
          </div>
        </div>

        <div class="amount-section">
          <div class="form-group">
            <label for="amount">Valor a Transferir</label>
            <div class="amount-input-wrapper">
              <span class="currency-prefix">R$</span>
              <input type="number" id="amount" formControlName="amount" placeholder="0,00">
            </div>
            <div class="balance-info" *ngIf="selectedSource">
              Saldo disponível: <span>{{selectedSource.valor | currency:'BRL'}}</span>
            </div>
            <div class="error-msg" *ngIf="f['amount'].touched && f['amount'].invalid">
              <span *ngIf="f['amount'].errors?.['required']">Valor é obrigatório</span>
              <span *ngIf="f['amount'].errors?.['min']">Valor deve ser maior que zero</span>
            </div>
            <div class="error-msg" *ngIf="f['amount'].value > (selectedSource?.valor || 0)">
              Saldo insuficiente para esta operação
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-outline" routerLink="/beneficios">Cancelar</button>
          <button type="submit" class="btn btn-primary" 
                  [disabled]="transferForm.invalid || loading || (f['amount'].value > (selectedSource?.valor || 0))">
            {{ loading ? 'Processando...' : 'Confirmar Transferência' }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-header { margin-bottom: 2.5rem; }
    h1 { font-size: 2.5rem; margin-bottom: 0.5rem; }
    .subtitle { color: var(--text-muted); }

    .transfer-card {
      padding: 3rem;
      max-width: 900px;
      margin: 0 auto;
    }

    .transfer-flow {
      display: flex;
      align-items: flex-end;
      gap: 2rem;
      margin-bottom: 3rem;
    }

    .flow-step { flex: 1; }

    .step-label {
      font-weight: 800;
      font-size: 0.75rem;
      color: var(--primary-color);
      margin-bottom: 0.75rem;
      letter-spacing: 0.1em;
    }

    .flow-arrow {
      padding-bottom: 1rem;
    }

    .arrow-icon {
      font-size: 2rem;
      color: var(--text-muted);
      opacity: 0.5;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    label { font-size: 0.875rem; color: var(--text-muted); font-weight: 500; }

    select, input {
      background: rgba(0, 0, 0, 0.2);
      border: 1px solid var(--border-color);
      border-radius: 0.5rem;
      padding: 0.875rem 1rem;
      color: white;
      outline: none;
      width: 100%;
      font-size: 1rem;
      transition: all 0.3s;
    }

    select:focus, input:focus { border-color: var(--primary-color); }

    .amount-section {
      max-width: 400px;
      margin: 0 auto;
    }

    .amount-input-wrapper {
      position: relative;
    }

    .currency-prefix {
      position: absolute;
      left: 1rem;
      top: 50%;
      transform: translateY(-50%);
      color: var(--text-muted);
      font-weight: 600;
    }

    .amount-input-wrapper input {
      padding-left: 3rem;
      font-size: 1.5rem;
      font-weight: 700;
      text-align: right;
    }

    .balance-info {
      margin-top: 0.5rem;
      font-size: 0.875rem;
      color: var(--text-muted);
    }

    .balance-info span { color: var(--success); font-weight: 600; }

    .error-msg { color: var(--danger); font-size: 0.8rem; margin-top: 0.4rem; }

    .form-actions {
      margin-top: 3.5rem;
      display: flex;
      justify-content: center;
      gap: 1.5rem;
    }

    @media (max-width: 768px) {
      .transfer-flow { flex-direction: column; align-items: stretch; }
      .flow-arrow { display: none; }
    }
  `]
})
export class TransferenciaComponent implements OnInit {
  transferForm!: FormGroup;
  ativos: Beneficio[] = [];
  selectedSource?: Beneficio;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private router: Router
  ) {}

  ngOnInit() {
    this.initForm();
    this.loadAtivos();
  }

  initForm() {
    this.transferForm = this.fb.group({
      fromId: [null, Validators.required],
      toId: [null, Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]]
    });
  }

  get f() { return this.transferForm.controls; }

  loadAtivos() {
    this.service.findAtivos().subscribe(data => {
      this.ativos = data;
    });
  }

  onSourceChange() {
    const id = this.transferForm.value.fromId;
    this.selectedSource = this.ativos.find(b => b.id === id);
    
    // Se o destino for igual ao escolhido na origem, limpa o destino
    if (this.transferForm.value.toId === id) {
      this.transferForm.patchValue({ toId: null });
    }
  }

  onSubmit() {
    if (this.transferForm.invalid) return;

    this.loading = true;
    const dto: TransferenciaDTO = this.transferForm.value;

    this.service.transfer(dto).subscribe({
      next: () => {
        alert('Transferência realizada com sucesso!');
        this.loading = false;
        this.router.navigate(['/beneficios']);
      },
      error: (err) => {
        alert(err.error?.message || 'Erro ao realizar transferência');
        this.loading = false;
      }
    });
  }
}
