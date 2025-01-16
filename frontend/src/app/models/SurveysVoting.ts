export enum questionType{
    SingleChoice,
    MutlitpleChoice,
    Scale,
    FreeAnswer // xdddddddd
}

export enum QuestionareType{
    Survey,
    Voting
}

export class Question{
    questionText: string = "";
    questionType: questionType = questionType.SingleChoice;
    answers: Answer[] = [];
    optional: boolean = false;
    id: number = -1;

    constructor(questionText?: string, questionTypeArg?: questionType, answers?: Answer[], optional?: boolean){
        this.questionText = questionText || "";
        this.questionType = questionTypeArg || questionType.SingleChoice;
        this.answers = answers || [];
        this.optional = optional || false;
    }

}

export class Answer{
    answerText: string | null = null;
    selected: boolean = false;
    id: number = -1;

    constructor(answerText?: string){
        this.answerText = answerText || "";
    }
}

export class Questionare{
    id: number = -1;
    name: string = "";
    questions: Question[] = [];
    type: QuestionareType = QuestionareType.Survey;
    authorName: string = "";
    isActive: boolean = true;

    convertToSurveyAnswerDto(){
        let dto = new SurveyResponse();
        dto.surveyId = this.id;
        for(let question of this.questions){
            let newAns = new SurveyAnswerResponse();
            newAns.questionId = question.id;
            let scaleVal = 1;
            let answered = false;

            for(let ans of question.answers){
                if(question.questionType === questionType.SingleChoice || question.questionType === questionType.MutlitpleChoice){
                    if(ans.selected){
                        answered = true;
                        newAns.surveyResponseChoices.push(new SurveyChoice(ans.id));
                    }
                }
                else if(question.questionType === questionType.Scale){
                    if(ans.selected){
                        answered = true;
                        newAns.scaleValue = scaleVal;
                    }
                    else{
                        scaleVal++;
                    }
                }
                else{
                    if(ans.answerText && ans.answerText.length > 0){
                        answered = true;
                        newAns.answerContent = ans.answerText;
                    }
                }
            }
            if(answered)
                dto.answers.push(newAns);
        }
        return dto;
    }

    processAnswers(answersFromBe: any){
        let result: Array<Array<number>> = [] //each subarray represents one question
        
        for(let question of this.questions){
            let res = []
            if(question.questionType === questionType.Scale){ //each number in array represents number of people selecting given choice
                let answer = answersFromBe.scaleQuestions.filter((obj: any) => obj.questionId === question.id)[0]
                let keys = Object.keys(answer.countsPerValue)
                for(let k of keys){
                    res[Number(k) - 1] = Number(answer.countsPerValue[k])
                }
            }   
            else if(question.questionType === questionType.MutlitpleChoice){ //each number in array represents percentage of all people selecting given option (sum ups to 100)
                let answer = answersFromBe.choiceQuestions.filter((obj: any) => obj.questionId === question.id)[0];
                console.log(answer)
                let countsPerChoice = answer.countsPerChoice;
                for(let ans of question.answers){
                    let count = Number(countsPerChoice[ans.id.toString()]);
                    let value = Math.round((((count * 100) / Number(answer.totalVotes)) * 100) / 100);
                    res.push(value)
                }
            }
            else if(question.questionType === questionType.SingleChoice){ //each number in array represents percentage out of 100% 
                let answer = answersFromBe.choiceQuestions.filter((obj: any) => obj.questionId === question.id)[0];
                console.log(answer)
                let countsPerChoice = answer.countsPerChoice;
                for(let ans of question.answers){
                    let count = Number(countsPerChoice[ans.id.toString()]);
                    let value = Math.round((((count * 100) / Number(answer.totalVotes)) * 100) / 100);
                    res.push(value)
                }
            }
            result.push(res);
        }

        return result;
    }

    convertToVotingAnswersDTO(){
        let resArr = [];
        for(let question of this.questions){
            for(let ans of question.answers){
                if(ans.selected){
                    resArr.push(ans.id);
                }
            }
        }
        return resArr;
    }
    processVotingAnswers(answersFromBe: any){
        let result: Array<Array<number>> = []; //each subarray represents one question
        for(let question of this.questions){
            let res = [];
            for(let ans of question.answers){
                let obj = answersFromBe.filter((obj: any) => obj.answerId === ans.id)[0];
                res.push(Math.round((obj.percentage * 100) / 100));
            }
            result.push(res);
        }
        return result;
    }
}

class VotingAnswersBe{
    content: string = "";
    voteCount: number = 0;
    votingAnsId: number = -1;

    constructor(responseFromBe?: any){
        if(!responseFromBe) return;
        this.content = responseFromBe.content;
        this.votingAnsId = responseFromBe.id;
    }

    convertQuestionareAnswer(answer: Answer){
        this.content = answer.answerText!;
    }

    convertToQuestionare(){
        let newAns = new Answer();
        newAns.answerText = this.content;
        newAns.id = this.votingAnsId;
        return newAns;
    }
}

class VotingQuestionBe{
    questionId: number = -1;
    content: string = "";
    mandatory: boolean = true;
    voteAnswers: VotingAnswersBe[] = [];
    voteCount: number=  0;

    constructor(responseFromBe?: any){
        if(!responseFromBe) return;
        this.questionId = responseFromBe.id;
        this.content = responseFromBe.content;
        this.mandatory = responseFromBe.mandatory;
        for(let ans of responseFromBe.voteAnswers){
            this.voteAnswers.push(new VotingAnswersBe(ans));
        }
    }

    convertQuestionareQuestion(question: Question){
        this.content = question.questionText;
        this.mandatory = question.optional;
        for(let ans of question.answers){
            let newAns = new VotingAnswersBe();
            newAns.convertQuestionareAnswer(ans);
            this.voteAnswers.push(newAns); 
        }
    }

    convertToQuestionare(){
        let newQuestion = new Question();
        newQuestion.id = this.questionId;
        newQuestion.optional = this.mandatory;
        newQuestion.questionType = questionType.SingleChoice;
        newQuestion.questionText = this.content;
        for(let ans of this.voteAnswers){
            newQuestion.answers.push(ans.convertToQuestionare());
        }
        return newQuestion;
    }
}

export class VotingBe{
    title: string = "";
    voteQuestions: VotingQuestionBe[] = [];
    votingId: number = -1;
    authorName: string = "";
    voteCount: number = 0;
    statusStr: string = "";

    constructor(responseFromBe?: any){
        if(!responseFromBe) return;
        console.log(responseFromBe)
        this.votingId = responseFromBe.id;
        this.title = responseFromBe.title;
        this.authorName = `${responseFromBe.firstname} ${responseFromBe.lastname}`;
        this.statusStr = responseFromBe.status;
        for(let question of responseFromBe.voteQuestions){
            this.voteQuestions.push(new VotingQuestionBe(question))
        }
    }

    convertQuestionareToBeDTO(questionare: Questionare){
        this.title = questionare.name;
        for(let question of questionare.questions){
            let newQuestion = new VotingQuestionBe();
            newQuestion.convertQuestionareQuestion(question);
            this.voteQuestions.push(newQuestion);
        }
    }

    convertToQuestionare(){
        let newQuestionare = new Questionare();
        newQuestionare.type = QuestionareType.Voting;
        newQuestionare.authorName = this.authorName;
        newQuestionare.name = this.title;
        newQuestionare.id = this.votingId;
        newQuestionare.isActive = this.statusStr === "ACTIVE";

        for(let question of this.voteQuestions){
            newQuestionare.questions.push(question.convertToQuestionare());
        }
        return newQuestionare;
    }
}

class SurveyAnswerBe{
    choiceText: string = "";
    choiceId: number = -1;

    constructor(answerFromBe?: any){
        if(!answerFromBe) return;
        this.choiceText = answerFromBe.choiceText;
        this.choiceId = answerFromBe.id;
    }

    convertToQuestionareAnswer(){
        let newAns = new Answer();
        newAns.id = this.choiceId;
        newAns.answerText = this.choiceText;

        return newAns;
    }
}

class SurveyQuestionBe{
    questionText: string = "";
    questionType: "TEXT" | "SINGLE_CHOICE" | "MULTIPLE_CHOICE" | "SCALE" = "TEXT";
    isOptional: boolean = false;
    choices: SurveyAnswerBe[] = [];
    questionId: number = -1;

    constructor(questionFromBe?: any){
        if(!questionFromBe) return;
        this.questionId = questionFromBe.id;
        this.questionText = questionFromBe.questionText;
        this.questionType = questionFromBe.questionType;
        this.isOptional = questionFromBe.isOptional;
        for(let choice of questionFromBe.choices){
            this.choices.push(new SurveyAnswerBe(choice));
        }
    }

    convertFEQuestionToBe(question: Question){
        this.questionText = question.questionText;
        this.isOptional = question.optional;
        if(question.questionType === questionType.Scale){
            this.questionType = "SCALE";
        }
        else if(question.questionType === questionType.FreeAnswer){
            this.questionType = "TEXT";
        }
        else if(question.questionType === questionType.MutlitpleChoice){
            this.questionType = "MULTIPLE_CHOICE";
        }
        else{
            this.questionType = "SINGLE_CHOICE";
        }
        if(question.questionType != questionType.Scale && question.questionType != questionType.FreeAnswer){
            for(let answer of question.answers){
                let ans = new SurveyAnswerBe();
                ans.choiceText = answer.answerText!;
                this.choices.push(ans)
            }
        }
    }

    convertToQuestionareQuestion(){
        let newQuestion = new Question();
        newQuestion.optional = this.isOptional;
        newQuestion.questionText = this.questionText;
        newQuestion.id = this.questionId;
        if(this.questionType === "SCALE"){
            newQuestion.questionType = questionType.Scale;
        }
        else if(this.questionType === "TEXT"){
            newQuestion.questionType = questionType.FreeAnswer;
        }
        else if(this.questionType === "MULTIPLE_CHOICE"){
            newQuestion.questionType = questionType.MutlitpleChoice;
        }
        else{
            newQuestion.questionType = questionType.SingleChoice;
        }

        if(newQuestion.questionType === questionType.Scale){
            newQuestion.answers = Array.from({length: 5}, () => new Answer());
        }
        else if(newQuestion.questionType === questionType.FreeAnswer){
            newQuestion.answers = [new Answer()];
        }
        else{
            for(let ans of this.choices){
                newQuestion.answers.push(ans.convertToQuestionareAnswer());
            }
        }

        return newQuestion;
    }
}

export class SurveyBe{
    title: string = "";
    surveyQuestions: SurveyQuestionBe[] = [];
    questionareId: number = -1;
    authorName: string = "";
    statusStr: string = "";

    constructor(responseFromBe?: any){
        if(!responseFromBe) return; 
        this.questionareId = responseFromBe.id;
        this.title = responseFromBe.title;
        this.authorName = `${responseFromBe.author.firstName} ${responseFromBe.author.lastName}`
        this.statusStr = responseFromBe.status;
        for(let question of responseFromBe.surveyQuestions){
            this.surveyQuestions.push(new SurveyQuestionBe(question))
        }
    }

    convertQuestionareToBeDTO(questionare: Questionare){
        this.title = questionare.name;
        for(let question of questionare.questions){
            let newQuest = new SurveyQuestionBe();
            newQuest.convertFEQuestionToBe(question);
            this.surveyQuestions.push(newQuest);
        }
    }

    convertToQuestionare(){
        let newQuestionare = new Questionare();
        newQuestionare.id = this.questionareId;
        newQuestionare.name = this.title;
        newQuestionare.type = QuestionareType.Survey;
        newQuestionare.authorName = this.authorName;
        newQuestionare.isActive = this.statusStr === "ACTIVE";

        for(let question of this.surveyQuestions){
            newQuestionare.questions.push(question.convertToQuestionareQuestion())
        }
        return newQuestionare;
    }
}

class SurveyChoice{
    questionChoiceId: number = -1;

    constructor(id: number){
        this.questionChoiceId = id;
    }
}

class SurveyAnswerResponse{
    questionId: number = -1;
    answerContent: string = "";
    surveyResponseChoices: SurveyChoice[] = [];
    scaleValue: number = -1;
}

export class SurveyResponse{
    surveyId: number = -1;
    answers: SurveyAnswerResponse[] = [];
}