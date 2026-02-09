import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BeneficioService } from '../../services/beneficio.service';
import { BeneficioUpdateDTO, BeneficioCreateDTO } from '../../models/beneficio.interface';

@Component({
  selector: 'app-beneficio-form',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="page-header fade-in">
      <h1>{{ isEditMode ? 'Editar Benefício' : 'Novo Benefício' }}</h1>
      <button class="btn btn-outline" routerLink="/beneficios">Voltar</button>
    </div>

    <div class="form-container glass-panel fade-in" style="animation-delay: 0.1s">
      <form [formGroup]="beneficioForm" (ngSubmit)="onSubmit()">
        <div class="form-grid">
          <div class="form-group">
            <label for="nome">Nome</label>
            <input type="text" id="nome" formControlName="nome" placeholder="Ex: Auxílio Alimentação">
            <div class="error-msg" *ngIf="f['nome'].touched && f['nome'].invalid">
              <span *ngIf="f['nome'].errors?.['required']">Nome é obrigatório</span>
              <span *ngIf="f['nome'].errors?.['maxlength']">Máximo 100 caracteres</span>
            </div>
          </div>

          <div class="form-group">
            <label for="valor">Valor (R$)</label>
            <input type="number" id="valor" formControlName="valor" placeholder="0.00">
            <div class="error-msg" *ngIf="f['valor'].touched && f['valor'].invalid">
              <span *ngIf="f['valor'].errors?.['required']">Valor é obrigatório</span>
              <span *ngIf="f['valor'].errors?.['min']">Valor deve ser maior que zero</span>
            </div>
          </div>

          <div class="form-group full-width">
            <label for="descricao">Descrição</label>
            <textarea id="descricao" formControlName="descricao" rows="4" placeholder="Descreva brevemente o benefício..."></textarea>
          </div>

          <div class="form-group checkbox">
            <label class="switch">
              <input type="checkbox" formControlName="ativo">
              <span class="slider round"></span>
            </label>
            <span>Benefício Ativo</span>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn btn-outline" routerLink="/beneficios">Cancelar</button>
          <button type="submit" class="btn btn-primary" [disabled]="beneficioForm.invalid || loading">
            {{ loading ? 'Salvando...' : (isEditMode ? 'Atualizar' : 'Criar') }}
          </button>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .page-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .form-container {
      padding: 2.5rem;
      max-width: 800px;
      margin: 0 auto;
    }

    .form-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1.5rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .full-width {
      grid-column: span 2;
    }

    label {
      font-weight: 500;
      color: var(--text-muted);
      font-size: 0.9rem;
    }

    input, textarea {
      background: rgba(0, 0, 0, 0.2);
      border: 1px solid var(--border-color);
      border-radius: 0.5rem;
      padding: 0.75rem 1rem;
      color: white;
      font-family: inherit;
      outline: none;
      transition: border-color 0.3s;
    }

    input:focus, textarea:focus {
      border-color: var(--primary-color);
    }

    .checkbox {
      flex-direction: row;
      align-items: center;
      gap: 1rem;
      margin-top: 1rem;
    }

    .error-msg {
      color: var(--danger);
      font-size: 0.8rem;
    }

    .form-actions {
      margin-top: 2.5rem;
      display: flex;
      justify-content: flex-end;
      gap: 1rem;
    }

    /* Toggle Switch Styles */
    .switch {
      position: relative;
      display: inline-block;
      width: 46px;
      height: 24px;
    }

    .switch input { opacity: 0; width: 0; height: 0; }

    .slider {
      position: absolute;
      cursor: pointer;
      top: 0; left: 0; right: 0; bottom: 0;
      background-color: rgba(255,255,255,0.1);
      transition: .4s;
    }

    .slider:before {
      position: absolute;
      content: "";
      height: 18px; width: 18px;
      left: 3px; bottom: 3px;
      background-color: white;
      transition: .4s;
    }

    input:checked + .slider { background-color: var(--success); }
    input:checked + .slider:before { transform: translateX(22px); }
    .slider.round { border-radius: 34px; }
    .slider.round:before { border-radius: 50%; }
  `]
})
export class BeneficioFormComponent implements OnInit {
  beneficioForm!: FormGroup;
  isEditMode = false;
  id?: number;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private service: BeneficioService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.isEditMode = !!this.id;

    this.initForm();

    if (this.isEditMode) {
      this.loadBeneficio();
    }
  }

  initForm() {
    this.beneficioForm = this.fb.group({
      nome: ['', [Validators.required, Validators.maxLength(100)]],
      descricao: [''],
      valor: [null, [Validators.required, Validators.min(0.01)]],
      ativo: [true]
    });
  }

  get f() { return this.beneficioForm.controls; }

  loadBeneficio() {
    this.loading = true;
    this.service.findById(this.id!).subscribe({
      next: (b) => {
        this.beneficioForm.patchValue(b);
        this.loading = false;
      },
      error: () => {
        alert('Erro ao carregar benefício');
        this.router.navigate(['/beneficios']);
      }
    });
  }

  onSubmit() {
    if (this.beneficioForm.invalid) return;

    this.loading = true;
    if (this.isEditMode) {
      const dto: BeneficioUpdateDTO = this.beneficioForm.value;
      this.service.update(this.id!, dto).subscribe({
        next: () => this.handleSuccess('Benefício atualizado com sucesso!'),
        error: (err) => this.handleError(err)
      });
    } else {
      const dto: BeneficioCreateDTO = this.beneficioForm.value;
      this.service.create(dto).subscribe({
        next: () => this.handleSuccess('Benefício criado com sucesso!'),
        error: (err) => this.handleError(err)
      });
    }
  }

  private handleSuccess(msg: string) {
    alert(msg);
    this.loading = false;
    this.router.navigate(['/beneficios']);
  }

  private handleError(err: any) {
    alert(err.error?.message || 'Erro ao salvar benefício');
    this.loading = false;
  }
}
