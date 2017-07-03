/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Component, EventEmitter, Input, OnInit, Output} from "@angular/core";
import {ClassifiedEntity, Sentence, SentenceStatus} from "../model/nlp";
import {StateService} from "../core/state.service";
import {Intent} from "../model/application";
import {NlpService} from "../nlp-tabs/nlp.service";
import {CreateIntentDialogComponent} from "./create-intent-dialog/create-intent-dialog.component";
import {MdDialog, MdSnackBar} from "@angular/material";

@Component({
  selector: 'tock-sentence-analysis',
  templateUrl: './sentence-analysis.component.html',
  styleUrls: ['./sentence-analysis.component.css']
})
export class SentenceAnalysisComponent implements OnInit {

  @Input() @Output() sentence: Sentence;
  @Input() displayArchiveButton: boolean = true;
  @Input() displayProbabilities: boolean = false;
  @Output() closed = new EventEmitter();
  @Input() displayEntities: Boolean = true;

  constructor(public state: StateService,
              private nlp: NlpService,
              private snackBar: MdSnackBar,
              private dialog: MdDialog) {
  }

  ngOnInit() {
  }

  onDeleteEntity(entity: ClassifiedEntity) {
    const entities = this.sentence.classification.entities;
    entities.splice(entities.indexOf(entity, 0), 1);
    this.sentence = this.sentence.clone();
  }

  onIntentChange(value) {
    //cleanup entities
    this.sentence.classification.entities = [];
    if (value === "newIntent") {
      let dialogRef = this.dialog.open(CreateIntentDialogComponent);
      dialogRef.afterClosed().subscribe(result => {
        if (result !== "cancel") {
          if (this.createIntent(result.name)) {
            return;
          }
        }
        //we need to be sure the selected value has changed to avoid side effects
        if (this.sentence.classification.intentId) {
          this.sentence.classification.intentId = undefined;
        } else {
          this.onIntentChange(Intent.unknown);
        }
      });
    } else {
      this.sentence.classification.intentId = value;
      this.sentence = this.sentence.clone();
    }
  }

  onLanguageChange(value) {
    //do nothing
  }

  onValidate() {
    const intent = this.sentence.classification.intentId;
    if (!intent || intent === Intent.unknown) {
      this.snackBar.open(`Please select an intent first`, "Error", {duration: 3000});
    } else {
      this.update(SentenceStatus.validated);
    }
  }

  onArchive() {
    this.sentence.classification.intentId = Intent.unknown;
    this.sentence.classification.entities = [];
    this.update(SentenceStatus.validated);
  }

  onDelete() {
    this.update(SentenceStatus.deleted);
  }

  private update(status: SentenceStatus) {
    this.sentence.status = status;
    this.nlp.updateSentence(this.sentence)
      .subscribe((s) => {
        this.closed.emit(this.sentence);
      });
    //delete old language
    if (this.sentence.language !== this.state.currentLocale) {
      const s = this.sentence.clone();
      s.language = this.state.currentLocale;
      s.status = SentenceStatus.deleted;
      this.nlp.updateSentence(s)
        .subscribe((s) => {
          this.snackBar.open(`Language change to ${this.state.localeName(this.sentence.language)}`, "Language change", {duration: 1000})
        });
    }

  }

  private createIntent(name): boolean {
    if (this.state.intentExists(name)) {
      this.snackBar.open(`Intent ${name} already exists`, "Error", {duration: 5000});
      return false
    } else {
      this.nlp.saveIntent(new Intent(name, this.state.user.organization, [], [this.state.currentApplication._id], null))
        .subscribe(intent => {
            this.state.currentApplication.intents.push(intent);
            this.onIntentChange(intent._id);
          },
          _ => this.onIntentChange(Intent.unknown));
      return true;
    }
  }

}
