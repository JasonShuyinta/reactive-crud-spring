package com.dotjson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private MessageMapper messageMapper;

    public Flux<MessageOutput> getMessages() {
        return messageRepository.findAll().map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> getMessage(String id) {
        return messageRepository.findById(id).map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> saveMessage(Mono<MessageInput> messageInputMono) {
        return messageInputMono.map(messageMapper::inputToEntity)
                .map(m -> {
                    m.setTime(LocalDateTime.now());
                    return m;
                })
                .flatMap(messageRepository::insert)
                .map(messageMapper::entityToOutput);
    }

    public Mono<MessageOutput> updateMessage(Mono<MessageInput> messageInputMono, String id) {
        return messageRepository.findById(id)
                .flatMap(m -> messageInputMono.map(messageMapper::inputToEntity)
                        .doOnNext(e->e.setId(id)))
                .flatMap(messageRepository::save)
                .map(messageMapper::entityToOutput);
    }

    public Mono<Void> deleteMessage(String id) {
        return messageRepository.deleteById(id);
    }
}
