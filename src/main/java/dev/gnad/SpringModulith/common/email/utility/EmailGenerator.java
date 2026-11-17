package dev.gnad.SpringModulith.common.email.utility;

import dev.gnad.SpringModulith.module.Task.domain.entity.Task;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class EmailGenerator {
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public static String generateOtpEmail(String otp){
        String htmlContent = """
                <html>
                    <body>
                        <h2>Password Reset</h2>

                        <p>
                            You requested to reset your password.
                            Please use the OTP below:
                        </p>

                        <h1 style="color: #2563eb;
                                   letter-spacing: 8px;">
                            %s
                        </h1>

                        <p>
                            This OTP will expire in 5 minutes.
                        </p>

                        <p>
                            If you did not request this operation,
                            please ignore this email.
                        </p>
                    </body>
                </html>
                """.formatted(otp);
        return htmlContent;
    }


    // 1. New method: one email for many to-do lists
    public static String generateDailyToDoEmail(List<ToDoList> toDoLists) {
        String sections = toDoLists.stream()
                .sorted(Comparator.comparing(ToDoList::getStartTime))
                .map(EmailGenerator::generateToDoSection)
                .collect(Collectors.joining("<hr>"));

        return """
    <html>
        <body style="font-family: Arial, sans-serif;
                     color: #333;
                     line-height: 1.6;">

            <h2 style="color: #2563eb;">
                Your ToDo Lists for Today
            </h2>

            <p>
                You have <strong>%d</strong> ToDo list(s) scheduled for today.
                Here is an overview of your tasks:
            </p>

            %s

            <hr>

            <p style="color: #666; font-size: 13px;">
                This is an automated reminder from your
                ToDo Management Application.
                Please review your tasks and update
                their progress accordingly.
            </p>

        </body>
    </html>
    """.formatted(toDoLists.size(), sections);
    }

    // 2. Old method kept, now delegates (existing callers still work)
    public static String generateToDoEmail(ToDoList todoList) {
        return generateDailyToDoEmail(List.of(todoList));
    }

    // 3. HTML for one to-do list (reused by both methods)
    private static String generateToDoSection(ToDoList todoList) {
        return """
        <div style="margin-bottom: 16px;">
            <h3>%s</h3>

            <p>
                <strong>Start Time:</strong> %s<br>
                <strong>Total Tasks:</strong> %d<br>
                <strong>Completed Tasks:</strong> %d<br>
                <strong>Remaining Tasks:</strong> %d
            </p>

            <h4>Task Details</h4>
            %s
        </div>
        """.formatted(
                todoList.getTopic(),
                todoList.getStartTime().format(TIME_FORMAT),
                todoList.getNumTask(),
                todoList.getNumCompleteTask(),
                todoList.getNumTask() - todoList.getNumCompleteTask(),
                generateTaskHtml(todoList.getTaskList())
        );
    }

    public static String generateTaskHtml(List<Task> tasks) {

        StringBuilder html = new StringBuilder();

        for (Task task : tasks) {

            html.append("""
                <div style="margin-bottom: 15px;
                            padding: 12px;
                            border: 1px solid #ddd;
                            border-radius: 6px;">

                    <h4 style="margin: 0 0 8px;">
                        %s
                    </h4>

                    <p style="margin: 4px 0;">
                        %s
                    </p>

                    <p style="font-size: 13px; color: #666;">
                        <strong>Expected Start:</strong> %s<br>
                        <strong>Expected End:</strong> %s<br>
                        <strong>Status:</strong>
                        <span style="color: %s;">
                            %s
                        </span>
                    </p>

                </div>
                """.formatted(
                    HtmlUtils.htmlEscape(task.getName()),
                    HtmlUtils.htmlEscape(task.getDescription()),
                    task.getExpectStart(),
                    task.getExpectEnd(),
                    task.isDone() ? "#16a34a" : "#dc2626",
                    task.isDone() ? "Completed" : "Pending"
            ));
        }

        return html.toString();
    }
}
