export class Article{
    author: string = "";
    title: string = "";
    text: string = "";
    id: number = -1;
    publishDate: Date = new Date();
    isActive: boolean = true;
}

export class ArticleResponse{
    content: string = "";
    firstname: string = "";
    lastname: string = "";
    likes: number = 0;
    publishDate: Date = new Date();
    title: string = "";
    id: number = -1;
    status: string = "";

    constructor(responseFromBE: any){
        this.content = responseFromBE.content;
        this.firstname = responseFromBE.firstname;
        this.lastname = responseFromBE.lastname;
        this.likes = responseFromBE.likes;
        this.publishDate = new Date(responseFromBE.publishDate);
        this.title = responseFromBE.title;
        this.id = responseFromBE.id;
        this.status = responseFromBE.status;
    }

    convertArticleResponseToArticle(): Article{
        let article: Article = new Article;
        article.id = this.id;
        article.author = `${this.firstname} ${this.lastname}`;
        article.title = this.title;
        article.text = this.content;
        article.publishDate = this.publishDate;
        article.isActive = this.status === "ACTIVE";
        return article;
    }
}

export class Comment{
    author: string = "";
    text: string = "";
    authorId: number = -1
}