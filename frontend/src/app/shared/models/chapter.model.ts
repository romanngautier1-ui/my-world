import { Book } from "./book.model";
import { ChapterRead } from "./chapter-read.model";
import { Comment } from "./comment.model";

export class Chapter {
    id: number;
    title: string;
    number: number;
    content: string;
    book: Book;
    createdAt: Date;
    likeCount: number;
    comments: Comment[];
    reads: ChapterRead[];

    constructor(id: number, title: string, number: number, content: string, book: Book, createdAt: Date, likeCount: number, comments: Comment[], reads: ChapterRead[]) {
        this.id = id;
        this.title = title;
        this.number = number;
        this.content = content;
        this.book = book;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.comments = comments;
        this.reads = reads;
    }   
}