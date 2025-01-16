import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, throwError } from 'rxjs';
import { PetitionSignatures, ProcessedData, ProcessedQuestions } from '../models/Export';
import jsPDF from "jspdf";
import Chart from 'chart.js/auto';
import "../../assets/Fonts/Roboto-Medium-normal.js";
import autoTable from 'jspdf-autotable';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class ExportService {

  constructor(private httpClient: HttpClient, private userService: UserService) { }

  colors = {
    "Modrá": "#000080",
    "Červená":"#ff0000",
    "Oranžová":"#ff8800",
    "Zelená":"#00ff3c",
    "Hnedá":"#964B00",
    "Fialová":"#9000ff",
    "Svetlomodrá":"#7391ff",
    "Svetlozelená":"#87fa8b",
    "Rúžová":"#ff00f7",
    "Čierna":"#000000",
    "Svetločervená":"#ff5959",
    "Žltá": "#fff700"
  }

  fetchData(url: string, useAuth: boolean){
    if(!useAuth){
      return this.httpClient.get(url, {observe: "response"}).pipe(
        map((res: HttpResponse<any>) => {
          return res.body;
        }),
        catchError((err: HttpErrorResponse) => {
          return throwError(() => new Error(`${err.status}`));
        })
      )
    }
    else{
      const token = sessionStorage.getItem("token");
      if(!token) return;
      
      let headers = new HttpHeaders();
      headers = headers.set("Authorization", `Bearer ${token}`);
      return this.httpClient.get(url, {headers, observe: "response"}).pipe(
        map((res: HttpResponse<any>) => {
          return res.body;
        }),
        catchError((err: HttpErrorResponse) => {
          return throwError(() => new Error(`${err.status}`));
        })
      )
    }
  }

  exportToCsv(processedData: ProcessedData){
    this.downloadCsv(this.convertToCsv(processedData), processedData.questionareName);
  }

  private convertToCsv(processedData: ProcessedData){
    let csvRows: string[] = [];
    const delimiter = ";;;;;\n";

    //header
    csvRows.push(delimiter);
    csvRows.push(`Typ:;${processedData.type};\n`);
    csvRows.push(`Názov:;${processedData.questionareName};\n`);
    csvRows.push(`Počet respondentov:;${processedData.numberOfRespondents};\n`);
    csvRows.push(delimiter);

    if(processedData.singleChoiceQuestions.length > 0){
      csvRows.push("Otázky s jednou možnosťou;\n");
      csvRows.push("Otázka;Počet respondentov;Možnosť;Počet odpovedí;Percentuálny podiel;\n");
      for(let i = 0; i < processedData.singleChoiceQuestions.length; i++){
        let question = processedData.singleChoiceQuestions[i];
        csvRows.push(`${question.questionName};${question.numberOfResponders};`);
        for(let j = 0; j < question.choices.length; j++){
          let ans = question.choices[j];
          if(j === 0){
            csvRows.push(`${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
          else{
            csvRows.push(`;;${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
        }
        csvRows.push(delimiter);
      }
    }
    csvRows.push(delimiter);

    if(processedData.multipleChoiceQuestions.length > 0){
      csvRows.push("Otázky s viacerými možnosťami;\n");
      csvRows.push("Otázka;Počet respondentov;Možnosť;Počet odpovedí;Percentuálny podiel;\n");
      for(let i = 0; i < processedData.multipleChoiceQuestions.length; i++){
        let question = processedData.multipleChoiceQuestions[i];
        csvRows.push(`${question.questionName};${question.numberOfResponders};`);
        for(let j = 0; j < question.choices.length; j++){
          let ans = question.choices[j];
          if(j === 0){
            csvRows.push(`${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
          else{
            csvRows.push(`;;${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
        }
        csvRows.push(delimiter);
      }
    }
    csvRows.push(delimiter);

    if(processedData.scaleQuestions.length > 0){
      csvRows.push("Otázky so stupnicou;\n");
      csvRows.push("Otázka;Počet respondentov;Možnosť;Počet odpovedí;Percentuálny podiel;\n");
      for(let i = 0; i < processedData.scaleQuestions.length; i++){
        let question = processedData.scaleQuestions[i];
        csvRows.push(`${question.questionName};${question.numberOfResponders};`);
        for(let j = 0; j < question.choices.length; j++){
          let ans = question.choices[j];
          if(j === 0){
            csvRows.push(`${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
          else{
            csvRows.push(`;;${ans.text};${ans.numberOfResponders};${ans.percentage};\n`);
          }
        }
        csvRows.push(delimiter);
      }
    }
    csvRows.push(delimiter);

    if(processedData.freeAnswerQuestions.length > 0){
      csvRows.push("Otázky s voľnou odpoveďou;\n");
      if(processedData.extended){
        csvRows.push("Otázka;Počet respondentov;Odpovede\n");
        for(let i = 0; i < processedData.freeAnswerQuestions.length; i++){
          let question = processedData.freeAnswerQuestions[i];
          csvRows.push(`${question.questionName};${question.numberOfResponders};`);
          for(let j = 0; j < question.answers.length; j++){
            let ans = question.answers[j];
            if(j === 0)
              csvRows.push(`${ans};\n`);
            else
              csvRows.push(`;;${ans};\n`);
          }
          csvRows.push(delimiter)
        }
      }
      else{
        csvRows.push("Otázka;Počet respondentov;\n");
        for(let i = 0; i < processedData.freeAnswerQuestions.length; i++){
          let question = processedData.freeAnswerQuestions[i];
          csvRows.push(`${question.questionName};${question.numberOfResponders};\n`);
        }
      }
    }

    return csvRows.join("");
  }

  private downloadCsv(csvData: string, questionareName: string){
    const bom = '\uFEFF';
    const blob = new Blob([bom + csvData], {type: "text/csv;charset=utf-8;"});
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = questionareName.replaceAll(" ", "_").concat("csv");
    a.style.display = 'none';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);

    URL.revokeObjectURL(url);
  }

  async exportToPdf(processedData: ProcessedData){
    await this.convertToPdf(processedData);
  }

  private async convertToPdf(processedData: ProcessedData){

    console.log(processedData)

    const pdf = new jsPDF();
    pdf.setFontSize(16);
    pdf.setFont("Roboto-Medium");
    
    this.addTitle(pdf, `${processedData.type}: ${processedData.questionareName}`);
    pdf.setFontSize(12);

    let idx = 0;

    if(processedData.singleChoiceQuestions.length > 0){
      pdf.text("Otázky s jednou možnosťou", 10, 17);
      for(let i = 0; i < processedData.singleChoiceQuestions.length; i++){
        let question = processedData.singleChoiceQuestions[i];
        await this.addSingleChoiceQuestion(pdf, question, idx++);
        if(idx === 2){
          pdf.addPage();
          idx = 0;
        }
      }
    }
    if(idx != 0){
      pdf.addPage();
      idx = 0;
    }

    if(processedData.multipleChoiceQuestions.length > 0){
      pdf.text("Otázky s viacerými možnosťami", 10, 17);
      for(let i = 0; i < processedData.multipleChoiceQuestions.length; i++){
        let question = processedData.multipleChoiceQuestions[i];
        await this.addSingleChoiceQuestion(pdf, question, idx++);
        if(idx === 2){
          pdf.addPage();
          idx = 0;
        }
      }
    }

    if(idx != 0){
      pdf.addPage();
      idx = 0;
    }

    if(processedData.scaleQuestions.length > 0){
      pdf.text("Otázky so škálou", 10, 17);
      for(let i = 0; i < processedData.scaleQuestions.length; i++){
        let question = processedData.scaleQuestions[i];
        await this.addScaleQuestion(pdf, question, idx++);
        if(idx === 4){
          pdf.addPage();
          idx = 0;
        }
      }
    }

    if(idx != 4){
      pdf.addPage();
      idx = 0;
    }

    if(processedData.freeAnswerQuestions.length > 0){
      pdf.text("Otázky s voľnou odpoveďou", 10, 17);
      for(let i = 0; i < processedData.freeAnswerQuestions.length; i++){
        let question = processedData.freeAnswerQuestions[i];
        this.addTextQuestion(pdf, question, idx++);
      }
    }

    pdf.save(`${processedData.questionareName.replaceAll(" ", "_")}.pdf`);
  }

  private addTitle(pdfObj: jsPDF, title: string){
    const pageWidth = pdfObj.internal.pageSize.width;
    const textWidth = pdfObj.getTextWidth(title);

    pdfObj.text(title, (pageWidth - textWidth) / 2, 10);
  }

  private addSubTitle(pdfObj: jsPDF, subtitle: string){
    const pageWidth = pdfObj.internal.pageSize.width;
    const textWidth = pdfObj.getTextWidth(subtitle);

    pdfObj.text(subtitle, (pageWidth - textWidth) / 2, 16);
  }

  private generateChart(chartOptions: any): Promise<string>{
    return new Promise((resolve) => {
      const chartCanvas = document.createElement('canvas');
      new Chart(chartCanvas, chartOptions);

      setTimeout(() => {
        const chartImage = chartCanvas.toDataURL('image/png');
        resolve(chartImage);
      }, 500);
    });
  }

  private async addSingleChoiceQuestion(pdfObj: jsPDF, question: ProcessedQuestions, idx: number){
    pdfObj.setFontSize(12);
    let titleY = 25 + (idx * 120);
    pdfObj.text(question.questionName, 10, titleY);

    let ansIdx = 0;
    let graphData = [];
    let graphLabels = [];

    let tableHeader = ["Možnosť", "Počet respondentov", "Percentuálny podiel", "Číslo v grafe"];
    let tableRows = [];

    for(let ans of question.choices){
      graphLabels.push(++ansIdx);
      graphData.push(ans.numberOfResponders);

      tableRows.push([ans.text, ans.numberOfResponders, ans.percentage, ansIdx])
    }

    autoTable(pdfObj, {
      head: [tableHeader],
      body: tableRows,
      startY: titleY + 5,
      styles: {
        font: 'Roboto-Medium',
        fontStyle: 'normal',
        fontSize: 10,       
      },
    })

    const graph = await this.generateChart({
      type: "doughnut",
      data: {
        labels: [...graphLabels],
        datasets: [{
          data: [...graphData],
          borderWidth: 1
        }]
      },
      options: {
        responsive: false,
        animation: false,
      }
    });

    if(ansIdx <= 3) ansIdx = 4;
    
    pdfObj.addImage(graph, "PNG", 80, titleY + (ansIdx * 11), 50, 50);
  }

  private async addScaleQuestion(pdfObj: jsPDF, question: ProcessedQuestions, idx: number){
    pdfObj.setFontSize(12);
    let titleY = 25 + (idx * 120);
    pdfObj.text(question.questionName, 10, titleY);

    let graphLabels = ["1", "2", "3", "4", "5"];
    let graphData = [];
    for(let choice of question.choices){
      graphData.push(choice.numberOfResponders);
    }

    const graph = await this.generateChart({
      type: "bar",
      data: {
        labels: [...graphLabels],
        datasets: [{
          data: [...graphData],
          borderWidth: 1
        }]
      },
      options: {
        responsive: false,
        animation: false,
        plugins: {
          legend: {
            display: false
          }
        }
      }
    });
    if(idx <= 3) idx = 4;
    
    pdfObj.addImage(graph, "PNG", 60, titleY + (idx * 2), 80, 40);
  }

  private addTextQuestion(pdfObj: jsPDF, question: ProcessedQuestions, idx: number){
    pdfObj.setFontSize(12);
    if(question.answers.length > 0){
      let titleY = idx === 0 ? 30 : 15;
      pdfObj.text(question.questionName, 10, titleY);
      pdfObj.text(`Počet respondentov: ${question.numberOfResponders}`, 10, titleY + 5);
      pdfObj.text("Odpovede:", 10, titleY + 10);
      
      const maxLineWidth = pdfObj.internal.pageSize.width - (2 * 10);
      const pageHeight = pdfObj.internal.pageSize.height;
      const initialPosition = titleY + 15;
      const lineHeight = 5;
      let currentPosition = initialPosition;

      for(let ans of question.answers){
        const lines = pdfObj.splitTextToSize(ans, maxLineWidth);

        lines.forEach((line: string, index: number) => {
          if(currentPosition + lineHeight > pageHeight - 5){
            pdfObj.addPage();
            currentPosition = 10;
          }
          pdfObj.text(line, 10, currentPosition);
          currentPosition += lineHeight;
        })
      }
      pdfObj.addPage();
    }
    else{
      let titleY = 25 + (idx * 15);
      pdfObj.text(question.questionName, 10, titleY);
      pdfObj.text(`Počet respondentov: ${question.numberOfResponders}`, 10, titleY + 5);
    } 
  }

  async exportSignaturesToPdf(signatures: PetitionSignatures[], author: string, title: string){
    const pdf = new jsPDF();
    pdf.setFontSize(16);
    pdf.setFont("Roboto-Medium");
    
    this.addTitle(pdf, title);
    pdf.setFontSize(14);
    this.addSubTitle(pdf, author);

    const tableHeader = ["", "Meno a priezvisko", "Ulica, č. domu", "Mesto/Obec"];
    let tableRows = [];
    let idx = 1;

    //signatures = Array.from({length: 250}, () => signatures[1]);

    for(let user of signatures){
      if(!user.verified) continue;
      let newRow = [idx++, `${user.user.firstName} ${user.user.lastName}`];
      if(user.user.address){
        newRow.push(`${user.user.address.street} ${user.user.address.houseNum}`, `${user.user.address.city.name}`);
      }
      tableRows.push(newRow);
    }

    pdf.setFontSize(12);
    pdf.text(`Počet podpísaných: ${idx - 1}`, 10, 20);

    autoTable(pdf, {
      head: [tableHeader],
      body: tableRows,
      startY: 20 + 5,
      styles: {
        font: 'Roboto-Medium',
        fontStyle: 'normal',
        fontSize: 10,       
      },
    })
    
    pdf.save(`Podpisy_${title.replaceAll(" ", "_")}`);
  }

  async exportSignarutesToCsv(signatures: PetitionSignatures[], author: string, title: string){
    let csvRows = [];

    csvRows.push(`Autor petície:; ${author};\nNázov petície:; ${title};\n`);
    csvRows.push("Zoznam podpísaných:;\n");
    csvRows.push(";\n");
    csvRows.push(";Meno a Priezvisko;Ulica, č. domu; Mesto/Obec;\n");

    let idx = 1;

    for(let user of signatures){
      if(!user.verified) continue;
      let newRow = `${idx++};${user.user.firstName} ${user.user.lastName};`;
      if(user.user.address){
        newRow = newRow.concat(`${user.user.address.street} ${user.user.address.houseNum};${user.user.address.city.name};`);
      }
      newRow = newRow.concat("\n");
      csvRows.push(newRow);
    }

    csvRows.splice(1, 0, `Počet podpísaných:; ${idx - 1};\n`);

    this.downloadCsv(csvRows.join(""), `Podpisy ${title}`);
  }
}