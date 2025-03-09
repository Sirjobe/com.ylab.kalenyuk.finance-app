# Finance App

Это консольное приложение для управления финансами, разработанное на Java. Оно позволяет пользователям регистрироваться, входить в систему, управлять транзакциями, бюджетами, целями и статистикой, а также предоставляет функции администрирования.

## Требования

- **Java**: JDK 17 (рекомендуется Amazon Corretto 17 или другая совместимая реализация OpenJDK).
- **Maven**: Версия 3.6.0 или выше для сборки проекта.
- **Операционная система**: Windows, macOS или Linux.

## Зависимости

Проект использует следующие зависимости, указанные в `pom.xml`:
- `junit-jupiter` (5.12.0) — для модульного тестирования.
- `mockito-core` и `mockito-junit-jupiter` (5.14.2) — для создания моков в тестах.
- `javax.mail` (1.6.2) — для имитации отправки уведомлений (не используется напрямую в текущей версии).

Полный список зависимостей доступен в файле `pom.xml`.

## Сборка проекта

Для сборки проекта используется Maven. Выполните следующие шаги:

1. https://github.com/Sirjobe/com.ylab.kalenyuk.finance-app:
   ```bash
   git clone <URL_репозитория>
   cd com.ylab.kalenyuk.finance-app
Соберите проект:
Выполните команду в корневой директории проекта (там, где находится pom.xml):
bash
mvn clean install
clean удаляет предыдущие сборки.
install компилирует код, запускает тесты и устанавливает JAR-файл в локальный репозиторий Maven.
Результатом будет файл target/com.ylab.kalenyuk.finance-app-1.0-SNAPSHOT.jar.
Примечание: Во время сборки могут появляться предупреждения вроде:
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
Это связано с механизмом Class Data Sharing (CDS) и не влияет на функциональность. Подробности см. в разделе "Частые вопросы".
Запуск приложения
После сборки вы можете запустить приложение:
Через Maven:
bash
mvn exec:java -Dexec.mainClass="com.ylab.App"
Это запустит главный класс App напрямую.
Через JAR-файл:
bash
java -jar target/com.ylab.kalenyuk.finance-app-1.0-SNAPSHOT.jar
Убедитесь, что вы находитесь в корневой директории проекта.
Взаимодействие:
После запуска вы увидите корневое меню:

Копировать
------------------------
Выберите пункт меню:
1. Главное меню
2. Меню пользователя
3. Администрирование
0. Выход
------------------------
Введите числа для навигации (например, 1 для главного меню, затем 1 для регистрации).
Запуск тестов
Тесты написаны с использованием JUnit 5 и Mockito. Для их выполнения:
Запустите тесты:
bash
mvn test
Тесты находятся в src/test/java/com/ylab/AppTest.java.
Результаты будут выведены в консоль, а подробные отчеты — в target/surefire-reports.
Проверка покрытия: Для анализа покрытия кода тестами можно добавить плагин jacoco-maven-plugin в pom.xml:
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.12</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
Затем выполните:
bash
mvn test
Отчет о покрытии будет в target/site/jacoco/index.html.
Структура проекта
src/main/java/com/ylab/ — основной код приложения.
App.java — точка входа.
entity/ — классы сущностей (User, Transaction, Budget, Goal).
management/ — менеджеры для работы с данными (UserManager, TransactionManager и др.).
src/test/java/com/ylab/ — тесты.
AppTest.java — тесты для класса App.
