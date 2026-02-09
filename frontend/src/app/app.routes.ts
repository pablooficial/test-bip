import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { BeneficiosListComponent } from './components/beneficios-list/beneficios-list.component';
import { BeneficioFormComponent } from './components/beneficio-form/beneficio-form.component';
import { TransferenciaComponent } from './components/transferencia/transferencia.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'beneficios', component: BeneficiosListComponent },
  { path: 'beneficios/novo', component: BeneficioFormComponent },
  { path: 'beneficios/editar/:id', component: BeneficioFormComponent },
  { path: 'transferir', component: TransferenciaComponent },
  { path: '**', redirectTo: 'dashboard' }
];
