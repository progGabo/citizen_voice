export class Petition{
    author: string = "";
    id: number = -1;
    title: string = "";
    numberOfSignees: number = -1;
    requiredNumOfSignees: number = -1;
    text: string = "";
    createdAt: Date = new Date();
    cityId: number = 0;
    isActive: boolean = true;
}

export class PetitionResponse{
    id: number = 0;
    name: string = "";
    noSignees: number = 0;
    requiredSignees: number = 0;
    content: string = "";
    firstName: string = "";
    lastName: string = "";
    createdAt: Date = new Date();
    cityId: number = 0;
    status: string = "";

    constructor(responseFromBE: any){
        this.content = responseFromBE.content;
        this.firstName = responseFromBE.firstName || "Nezname";
        this.lastName = responseFromBE.lastName || "Neni";
        this.createdAt = new Date(responseFromBE.createdAt);
        this.name = responseFromBE.name;
        this.id = responseFromBE.id;
        this.noSignees = responseFromBE.noSignees;
        this.requiredSignees = responseFromBE.requiredSignees;
        this.cityId = responseFromBE.cityId;
        this.status = responseFromBE.status; 
    }

    convertPetitionResponseToPetition(){
        let petition = new Petition();
        petition.author = `${this.firstName} ${this.lastName}`;
        petition.createdAt = this.createdAt;
        petition.id = this.id;
        petition.numberOfSignees = this.noSignees;
        petition.requiredNumOfSignees = this.requiredSignees;
        petition.text = this.content;
        petition.title = this.name;
        petition.cityId = this.cityId;
        petition.isActive = this.status === "ACTIVE";
        return petition;
    }
}

export class SignedPetitions{
    id: number = -1;
    name: string = "";
    noSignees: number = -1;
    requiredSignees: number = -1;
    status: string = "ACTIVE";
    createdAt: Date = new Date();
}