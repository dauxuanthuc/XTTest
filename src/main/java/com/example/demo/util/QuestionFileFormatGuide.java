package com.example.demo.util;

/**
 * Question File Format Documentation
 * 
 * EXCEL FORMAT (.xlsx):
 * - Row 1: Header (Question | Option A | Option B | Option C | Option D | Answer)
 * - Rows 2+: Question data
 * 
 * Example:
 * | Question | Option A | Option B | Option C | Option D | Answer |
 * | Uỷ ban Nhân dân có quyền ban hành những loại văn bản quy phạm pháp luật nào | Nghị định, nghị quyết | Quyết định, chỉ thị | Quyết định, chỉ thị, thông tư | Nghị định, nghị quyết, quyết định | B |
 * 
 * JSON FORMAT (.json):
 * [
 *   {
 *     "question": "Uỷ ban Nhân dân có quyền ban hành những loại văn bản quy phạm pháp luật nào",
 *     "optionA": "Nghị định, nghị quyết",
 *     "optionB": "Quyết định, chỉ thị",
 *     "optionC": "Quyết định, chỉ thị, thông tư",
 *     "optionD": "Nghị định, nghị quyết, quyết định",
 *     "answer": "B"
 *   },
 *   ...
 * ]
 * 
 * WORD FORMAT (.docx):
 * 1. Question text here
 * A. Option A text
 * B. Option B text
 * C. Option C text
 * D. Option D text
 * Đáp án: B
 * 
 * 2. Next question text
 * A. Option A text
 * B. Option B text
 * C. Option C text
 * D. Option D text
 * Đáp án: A
 */
public class QuestionFileFormatGuide {
    // This is a documentation class - no implementation needed
}
