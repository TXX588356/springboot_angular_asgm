import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  // Indicates whether a login request is currently in progress.
  loading = signal<boolean>(false)
  // Stores the login error message shown to the user.
  error = signal<string>('')

  // Reactive form that captures login credentials.
  loginForm: FormGroup

  // Builds the login form and injects authentication/navigation dependencies.
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    // Authentication requirement: login uses a validated reactive form before calling the API.
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]],
    })
  }

  // Redirects already-authenticated browser users away from the login page.
  ngOnInit(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return
    }

    this.authService.isAuthenticated().subscribe((authenticated) => {
      if (authenticated) {
        this.router.navigate(['/dashboard'])
      }
    })
  }

  // Validates the form, submits credentials, and navigates to the dashboard on success.
  login(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched()
      return
    }

    this.loading.set(true)
    this.error.set('')

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.router.navigate(['/dashboard'])
      },
      error: () => {
        this.error.set('Invalid email or password')
        this.loading.set(false)
      }
    })
  }
}
