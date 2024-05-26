package com.yupi.yuojbackendmodel.model.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author tangx
 * @Date 2024/5/20 23:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitMessage implements Serializable{

    private static final long serialVersionUID = 1L;

    private long messageId;

    private long questionSubmitId;
}
