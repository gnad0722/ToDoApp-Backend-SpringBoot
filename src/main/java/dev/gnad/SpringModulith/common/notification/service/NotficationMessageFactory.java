package dev.gnad.SpringModulith.common.notification.service;

import dev.gnad.SpringModulith.common.notification.domain.MessageContent;
import dev.gnad.SpringModulith.common.notification.domain.MessageType;


public class NotficationMessageFactory {

    public static MessageContent create(
            MessageType type,
            String resourceName
    ) {
        return switch (type) {
            case TODO_LIST_CREATED -> new MessageContent(
                    "ToDo List Created",
                    "Your ToDo List '" + resourceName
                            + "' has been created successfully."
            );

            case TODO_LIST_UPDATED -> new MessageContent(
                    "ToDo List Updated",
                    "Your ToDo List '" + resourceName
                            + "' has been updated successfully."
            );


            case TASK_CREATED -> new MessageContent(
                    "Task Created",
                    "Your task '" + resourceName
                            + "' has been created successfully."
            );

            case TASK_UPDATED -> new MessageContent(
                    "Task Updated",
                    "Your task '" + resourceName
                            + "' has been updated successfully."
            );

            case TODO_LIST_DUE -> new MessageContent(
                    "To Do List Starts Today",
                    "Your To Do List '" + resourceName
                            + "' is scheduled to start today."
            );



        };
    }
}
