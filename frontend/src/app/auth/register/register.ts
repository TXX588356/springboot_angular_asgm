import { isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register implements OnInit {
  // Indicates whether a registration request is currently in progress.
  loading = signal<boolean>(false)
  // Stores the registration error message shown to the user.
  error = signal<string>('')

  // Reactive form that captures new account details.
  registerForm: FormGroup

  // Builds the registration form and injects authentication/navigation dependencies.
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    // Authentication requirement: registration validates username, email, and password client-side.
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    })
  }

  // Redirects already-authenticated browser users away from the registration page.
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

  // Validates the form, submits account details, and sends the user to login on success.
  register(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched()
      return
    }

    this.loading.set(true)
    this.error.set('')

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.router.navigate(['/login'])
      },
      error: () => {
        this.error.set('Registration failed. Username or email may already exist.')
        this.loading.set(false)
      }
    })
  }
}
