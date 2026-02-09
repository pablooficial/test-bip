import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <nav class="navbar glass-panel">
      <div class="nav-container">
        <div class="logo">
          <span class="logo-icon">💠</span>
          <span class="logo-text">Benefit<span>X</span></span>
        </div>
        <div class="nav-links">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-link">Dashboard</a>
          <a routerLink="/beneficios" routerLinkActive="active" class="nav-link">Benefícios</a>
          <a routerLink="/transferir" routerLinkActive="active" class="nav-link">Transferência</a>
        </div>
      </div>
    </nav>

    <main class="container">
      <router-outlet></router-outlet>
    </main>

    <footer class="footer">
      <p>&copy; 2026 BenefitX - Gestão Inteligente de Benefícios</p>
    </footer>
  `,
  styles: [`
    .navbar {
      margin: 1.5rem 2rem;
      padding: 1rem 2rem;
      position: sticky;
      top: 1rem;
      z-index: 1000;
    }

    .nav-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      font-size: 1.5rem;
      font-weight: 700;
      font-family: 'Outfit', sans-serif;
    }

    .logo-text span {
      color: var(--secondary-color);
    }

    .nav-links {
      display: flex;
      gap: 1.5rem;
    }

    .nav-link {
      text-decoration: none;
      color: var(--text-muted);
      font-weight: 500;
      transition: all 0.3s ease;
      padding: 0.5rem 1rem;
      border-radius: 0.5rem;
    }

    .nav-link:hover, .nav-link.active {
      color: var(--text-main);
      background: rgba(255, 255, 255, 0.05);
    }

    .nav-link.active {
      border-bottom: 2px solid var(--primary-color);
      border-radius: 0.5rem 0.5rem 0 0;
    }

    .footer {
      text-align: center;
      padding: 3rem;
      color: var(--text-muted);
      font-size: 0.875rem;
    }
  `]
})
export class AppComponent {}
