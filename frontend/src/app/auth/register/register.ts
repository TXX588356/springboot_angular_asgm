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
  loading = signal<boolean>(false)
  error = signal<string>('')

  registerForm: FormGroup

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
