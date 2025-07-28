package com.encapsulearn.quiz_api.util;

import com.encapsulearn.quiz_api.entity.QuizOption;
import com.encapsulearn.quiz_api.entity.Question;
import com.encapsulearn.quiz_api.enums.QuestionType;
import com.encapsulearn.quiz_api.entity.Quiz;
import com.encapsulearn.quiz_api.enums.QuizType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelHelper {

    public static List<Quiz> parseExcelToQuizzes(InputStream is) throws IOException {
        Workbook workbook = new XSSFWorkbook(is);
        List<Quiz> quizzes = new ArrayList<>();
        Map<String, Quiz> quizMap = new HashMap<>();

        Sheet quizSheet = workbook.getSheet("Quizzes");
        if (quizSheet == null) {
            throw new IOException("Excel file must contain a sheet named 'Quizzes'.");
        }
        Iterator<Row> quizRows = quizSheet.iterator();
        int rowNum = 0;
        while (quizRows.hasNext()) {
            Row currentRow = quizRows.next();
            if (rowNum == 0) {
                rowNum++;
                continue;
            }

            Quiz quiz = new Quiz();
            quiz.setQuestions(new ArrayList<>());

            Cell titleCell = currentRow.getCell(0);
            Cell durationCell = currentRow.getCell(1);
            Cell typeCell = currentRow.getCell(2);

            if (titleCell == null || titleCell.getCellType() != CellType.STRING || titleCell.getStringCellValue().isEmpty()) {
                throw new IOException("Quiz title cannot be empty at row " + (rowNum + 1));
            }
            quiz.setTitle(titleCell.getStringCellValue());

            if (durationCell == null || durationCell.getCellType() != CellType.NUMERIC) {
                throw new IOException("Quiz duration must be a number at row " + (rowNum + 1));
            }
            quiz.setDurationMinutes((int) durationCell.getNumericCellValue());

            if (typeCell == null || typeCell.getCellType() != CellType.STRING || typeCell.getStringCellValue().isEmpty()) {
                throw new IOException("Quiz type cannot be empty at row " + (rowNum + 1));
            }
            try {
                quiz.setType(QuizType.valueOf(typeCell.getStringCellValue().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid Quiz Type '" + typeCell.getStringCellValue() + "' at row " + (rowNum + 1) + ". Must be MCQ or SHORT_ANSWER.");
            }

            quizzes.add(quiz);
            quizMap.put(quiz.getTitle(), quiz);
            rowNum++;
        }

        Sheet questionSheet = workbook.getSheet("Questions");
        if (questionSheet == null) {
            throw new IOException("Excel file must contain a sheet named 'Questions'.");
        }
        Iterator<Row> questionRows = questionSheet.iterator();
        rowNum = 0;
        while (questionRows.hasNext()) {
            Row currentRow = questionRows.next();
            if (rowNum == 0) {
                rowNum++;
                continue;
            }

            Cell quizTitleCell = currentRow.getCell(0);
            Cell questionTextCell = currentRow.getCell(1);
            Cell questionTypeCell = currentRow.getCell(2);
            Cell optionACell = currentRow.getCell(3);
            Cell optionBCell = currentRow.getCell(4);
            Cell optionCCell = currentRow.getCell(5);
            Cell optionDCell = currentRow.getCell(6);
            Cell correctOptionCell = currentRow.getCell(7);
            Cell correctAnswerShortCell = currentRow.getCell(8);

            String quizTitle = (quizTitleCell != null) ? quizTitleCell.getStringCellValue() : "";
            Quiz parentQuiz = quizMap.get(quizTitle);
            if (parentQuiz == null) {
                throw new IOException("Quiz '" + quizTitle + "' referenced in Questions sheet not found in Quizzes sheet at row " + (rowNum + 1));
            }

            Question question = new Question();
            question.setQuiz(parentQuiz);

            if (questionTextCell == null || questionTextCell.getCellType() != CellType.STRING || questionTextCell.getStringCellValue().isEmpty()) {
                throw new IOException("Question text cannot be empty at row " + (rowNum + 1));
            }
            question.setText(questionTextCell.getStringCellValue());

            if (questionTypeCell == null || questionTypeCell.getCellType() != CellType.STRING || questionTypeCell.getStringCellValue().isEmpty()) {
                throw new IOException("Question type cannot be empty at row " + (rowNum + 1));
            }
            try {
                question.setType(QuestionType.valueOf(questionTypeCell.getStringCellValue().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid Question Type '" + questionTypeCell.getStringCellValue() + "' at row " + (rowNum + 1) + ". Must be MCQ or SHORT_ANSWER.");
            }

            if (question.getType() == QuestionType.MCQ) {
                List<QuizOption> options = new ArrayList<>();
                String correctOptionIndicator = (correctOptionCell != null) ? correctOptionCell.getStringCellValue().trim().toUpperCase() : "";

                addOptionIfPresent(options, optionACell, "A", correctOptionIndicator, question);
                addOptionIfPresent(options, optionBCell, "B", correctOptionIndicator, question);
                addOptionIfPresent(options, optionCCell, "C", correctOptionIndicator, question);
                addOptionIfPresent(options, optionDCell, "D", correctOptionIndicator, question);

                if (options.isEmpty()) {
                    throw new IOException("MCQ question must have at least one option at row " + (rowNum + 1));
                }
                if (options.stream().noneMatch(QuizOption::isCorrect)) {
                    throw new IOException("MCQ question must have one correct option specified by 'CorrectOption' column at row " + (rowNum + 1));
                }
                question.setOptions(options);

            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                if (correctAnswerShortCell == null || correctAnswerShortCell.getCellType() != CellType.STRING || correctAnswerShortCell.getStringCellValue().isEmpty()) {
                    throw new IOException("Short Answer question must have a 'CorrectAnswerShort' at row " + (rowNum + 1));
                }
                question.setCorrectAnswer(correctAnswerShortCell.getStringCellValue());
            }

            parentQuiz.getQuestions().add(question);
            rowNum++;
        }

        workbook.close();
        return quizzes;
    }

    private static void addOptionIfPresent(List<QuizOption> options, Cell cell, String indicator, String correctOptionIndicator, Question question) {
        if (cell != null && cell.getCellType() == CellType.STRING && !cell.getStringCellValue().isEmpty()) {
            QuizOption option = new QuizOption();
            option.setText(cell.getStringCellValue());
            option.setCorrect(indicator.equals(correctOptionIndicator));
            option.setQuestion(question);
            options.add(option);
        }
    }
}
