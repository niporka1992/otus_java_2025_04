# Домашнее задание: Свой тестовый фреймворк

## Цель

- Научиться работать с reflection и аннотациями.
- Понять принцип работы фреймворка JUnit.

## Описание / Пошаговая инструкция выполнения домашнего задания

Написать свой тестовый фреймворк.

Поддержать свои аннотации `@Test`, `@Before`, `@After`.

Запускать вызовом статического метода с именем класса с тестами.

## Задание

1. Создать три аннотации — `@Test`, `@Before`, `@After`.
2. Создать класс-тест, в котором будут методы, отмеченные аннотациями.
3. Создать "запускалку теста". На вход она должна получать имя класса с тестами, в котором следует найти и запустить методы, отмеченные аннотациями из пункта 1.
4. Алгоритм запуска должен быть следующим:
    - метод(ы) `@Before`
    - текущий метод `@Test`
    - метод(ы) `@After`
    - для каждой такой "тройки" нужно создавать **свой экземпляр класса-теста**.
5. Исключение в одном тесте не должно прерывать весь процесс тестирования.
6. На основании возникших во время тестирования исключений необходимо вывести статистику выполнения тестов (сколько прошло успешно, сколько упало, сколько было всего).
7. "Запускалка теста" не должна иметь состояние, но при этом весь функционал должен быть разбит на приватные методы.
8. Надо придумать, как передавать информацию между методами.

---

## Запуск тестов

### Вариант 1 — Запуск из `Main.java`

Перейди в файл:  
`hw03-annotation/src/main/java/ru/otus/Main.java`  
и запусти его из IDE (например, IntelliJ IDEA) как обычное Java-приложение.

---

### Вариант 2 — Запуск через Gradle

Воспользуйся **Gradle UI** (вкладка *Gradle* в IDE) и найди задачу:  
`custom_test → run`

---

## Сборка и запуск

### Вариант 1 — собрать fat JAR только для модуля:

```bash
./gradlew :hw03-annotation:shadowJar
```

### Вариант 2 — собрать всё через корневой `build.gradle.kts`:

```bash
./gradlew build
```
