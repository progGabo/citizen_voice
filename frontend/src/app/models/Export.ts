export class ProcessedData{
    type: "Prieskum" | "Hlasovanie" = "Hlasovanie";
    questionareName: string = "";
    numberOfRespondents: number = -1;
    singleChoiceQuestions: ProcessedQuestions[] = [];
    multipleChoiceQuestions: ProcessedQuestions[] = [];
    scaleQuestions: ProcessedQuestions[] = [];
    freeAnswerQuestions: ProcessedQuestions[] = [];
    extended: boolean = false;
}
  
export class ProcessedQuestions{
    questionName: string = "";
    choices: ProcessedChoices[] = [];
    numberOfResponders: number = -1;
    answers: string[] = []; //used with free answers, contains all answers for given question
}
  
export class ProcessedChoices{
    text: string = "";
    numberOfResponders: number = -1;
    percentage: number = -1;
}

export class QuestionareForProcessing{
    surveyId: number = -1;
    totalResponses: number = 0;
    scaleQuestions: ScaleQuestion[] = [];
    choiceQuestions: ChoiceQuestion[] = [];
    textQuestions: TextQuestion[] = [];
}

export class ScaleQuestion{
    questionId: number = -1;
    totalVotes: number = 0;
    countsPerValue: {[name: string]: number} = {}
}

export class ChoiceQuestion{
    questionId: number = -1;
    totalVotes: number = 0;
    countsPerChoice: {[name: string]: number} = {}
}

export class TextQuestion{
    questionId: number = -1;
    totalAnswers: number = 0;
    answers: string[] = [];
}

export class PetitionSignatures{
    user: PetitionUser = new PetitionUser();
    verified: boolean = false;
}

class PetitionUser{
    firstName: string = "";
    lastName: string = "";
    address: Address | null = null; 
}

class Address{
    houseNum: number = -1;
    street: string = "";
    city: { name: string } = {name: ""};
}