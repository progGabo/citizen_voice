import { Component, Input } from '@angular/core';
import { Comment } from '../../models/Articles';

@Component({
  selector: 'app-comment',
  templateUrl: './comment.component.html',
  styleUrl: './comment.component.scss'
})
export class CommentComponent {
  @Input() comment: Comment = new Comment();
  @Input() isAuthorComment: boolean = false;
  @Input() isLastComment: boolean = false;
}
