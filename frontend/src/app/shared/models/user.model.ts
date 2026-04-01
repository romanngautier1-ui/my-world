import { ChapterRead } from "./chapter-read.model";

export class User {
    id: number;
    email: string;
    username: string;
    password: string;
    role: Role;
    createdAt: Date;
    isActive: boolean;
    chapterReads: ChapterRead[];

    constructor(id: number, username: string, email: string, password: string, role: Role, createdAt: Date, isActive: boolean, chapterReads: ChapterRead[]) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.isActive = isActive;
        this.chapterReads = chapterReads;
    }
}

export enum Role {
    USER = 'USER',
    ADMIN = 'ADMIN'
}