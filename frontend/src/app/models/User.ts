export enum UserRole{
    ROLE_USER,
    ROLE_ORGANIZATION
}

export enum Gender{
    MALE,
    FEMALE
}

export class RegisterData{
    email: string = "";
    dateOfBirth: string = "";
    role: string = UserRole[UserRole.ROLE_USER];
    gender: string = Gender[Gender.MALE];
    passwd: string = "";
    firstName: string = "";
    lastName: string = "";
}

export class LoginData{
    email: string = "";
    password: string = "";
    rememberMe: boolean = true; 
}

export class UserDetail{
    activeStatus: string = "";
    alertsOn: boolean = true;
    createdAt: string = "";
    dateOfBirth: string = "";
    email: string = "";
    firstName: string = "";
    gender: string = Gender[Gender.MALE];
    lastName: string = "";
    role: string = UserRole[UserRole.ROLE_USER];
    title: string | null = null;
    updatedAt: string = "";
}