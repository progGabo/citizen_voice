import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../../../services/user.service';
import { ErrorComponent } from '../error/error.component';
import { RegisterData, UserRole } from '../../../models/User';
import { SuccessComponent } from '../success/success.component';

@Component({
  selector: 'app-login-register-dialog',
  templateUrl: './login-register-dialog.component.html',
  styleUrls: ['./login-register-dialog.component.scss']
})
export class LoginRegisterDialogComponent {
  isLogin: boolean = true; // Toggle login/register
  loginForm: FormGroup;
  registerForm: FormGroup;
  registrationInProgress: boolean = false;
  registrationSuccess: boolean = false;
  registrationSuccessText: string = "";


  constructor(
    public dialogRef: MatDialogRef<LoginRegisterDialogComponent>,
    private fb: FormBuilder,
    private userService: UserService,
    private dialog: MatDialog
  ) {
    this.dialogRef.disableClose = false;

    // Initialize
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(4)]]
    });

    this.registerForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        password: ['', [Validators.required, Validators.minLength(4)]],
        confirm_password: ['', Validators.required],
        dateOfBirth: ['', Validators.required],
        receiveNotifications: [false],
        registerAsOrg: [false]
      },
      {
        validators: [this.passwordMatchValidator, this.ageValidator]
      }
    );
  }

  passwordMatchValidator(formGroup: AbstractControl){
      const password = formGroup.get("password")?.value
      const confirm_password = formGroup.get("confirm_password")?.value
      if(password && confirm_password && password != confirm_password){
        formGroup.get("confirm_password")?.setErrors({passwordsMismatch: true});
      }
      else{
        formGroup.get("confirm_password")?.setErrors(null);
      }
      return null;
  }

  ageValidator(formGroup: AbstractControl){
    const enteredDate = formGroup.get("dateOfBirth")?.value
    if(enteredDate){
      const date = new Date(enteredDate);
      const today = new Date();
      today.setHours(0,0,0,0);
      if(today.getTime() - date.getTime() <= 0){
        formGroup.get("dateOfBirth")?.setErrors({invalidDate: true});
      }
      else{
        formGroup.get("dateOfBirth")?.setErrors(null);
      }
    }
    return null;
  }

  onToggleChange(event: any) {
    this.isLogin = event.value === 'Login';
  }

  onLogin() {
    if (this.loginForm.valid) {
      this.loginForm.value.rememberMe = true; //added to be consistent with LoginData from User.ts

      this.userService.login(this.loginForm.value).subscribe({
        next: (data) => {
          data?.subscribe(res => {
            this.dialogRef.close({data: this.loginForm.value, successful: res.success, isAdmin: res.isAdmin, userName: res.userName}) // successful a isAdmin sa ziska z userService 
          })
          
        },
        error: (error) => {
          if(error.message === "404"){
            this.dialog.open(ErrorComponent, {data: {text: "Neexistuje účet s danou emailovou adresou"}})
          }
          else if(error.message === "401"){
            this.dialog.open(ErrorComponent, {data: {text: "Zadali ste zlé heslo"}})
          }
          else if(error.message === "403"){
            this.dialog.open(ErrorComponent, {data: {text: "Nemáte overený email, alebo váš účet čaká na schválenie administrátorom."}})
          }
          else{
            this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba"}})
          }
        }
      })
    }
    else{
      console.log("invalid form")
    }
  }

  onRegister() {
    if (this.registerForm.valid) {
      this.registrationInProgress = true;

      let registerData: RegisterData = new RegisterData;
      registerData.dateOfBirth = this.registerForm.value['dateOfBirth'];
      registerData.email = this.registerForm.value['email'];
      registerData.firstName = this.registerForm.value['firstName'];
      registerData.lastName = this.registerForm.value['lastName'];
      registerData.passwd = this.registerForm.value['password'];
      registerData.role = this.registerForm.value['registerAsOrg'] ? UserRole[UserRole.ROLE_ORGANIZATION] : UserRole[UserRole.ROLE_USER];

      //registerData.recieveNotification = this.registerForm.value['receiveNotifications'];
      
      this.userService.register(registerData).subscribe({
        next: (data) => {
          this.registrationSuccess = true
          if(registerData.role === UserRole[UserRole.ROLE_ORGANIZATION]){
            this.registrationSuccessText = `Na email ${registerData.email}, bol odoslaný link na potvrdenie. Po potvrdení registrácie musíte počkať na schválenie administrátorom.`;
          }
          else{
            this.registrationSuccessText = `Na email ${registerData.email}, bol odoslaný link na potvrdenie.`
          }
        },
        error: (error) => {
          this.registrationInProgress = false;
          if(error.message === "400"){
            this.dialog.open(ErrorComponent, {data: {text: "Používateľ so zadaným mailom už existuje."}})
          }
          else{
            this.dialog.open(ErrorComponent, {data: {text: "Nastala neočakávaná chyba"}})
          }
          
        }
      })
      
    }
  }
}
