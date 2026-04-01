import { User } from "./user.model";

export class Article {
    id: number;
    titre: string;
    contenu: string;
    user: User;
    urlImage: string | null;
    dateCreation: Date;

    
    constructor(id: number, titre: string, contenu: string, user: User, urlImage: string | null, dateCreation: Date) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.user = user;
        this.urlImage = urlImage;
        this.dateCreation = dateCreation;
    }
}

export type ArticleList = Article[];