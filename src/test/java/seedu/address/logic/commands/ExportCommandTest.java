package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.util.FileUtil;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.ExportManager;

public class ExportCommandTest {

    private String expected = "00001,Alice Pauline\n"
            + "00002,Benson Meier\n"
            + "00003,Carl Kurz\n"
            + "00004,Daniel Meier\n"
            + "00005,Elle Meyer\n"
            + "00006,Fiona Kunz\n"
            + "00007,George Best\n";
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Path testPath = Path.of("./exports/test.csv");

    @Test
    public void execute_exportSuccessful() throws Exception {
        ExportManager mockExportManager = new ExportManagerStub();
        ExportCommand exportCommand = new ExportCommand(mockExportManager, testPath);

        String expectedMessage = ExportCommand.MESSAGE_SUCCESS;
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);

        CommandResult result = exportCommand.execute(model);
        assertEquals(expectedCommandResult, result);

        String expectedExportedContent = expected;
        assertEquals(expectedExportedContent, ((ExportManagerStub) mockExportManager).getExportedContent());
    }

    @Test
    public void execute_exportSuccessfulWithDuplicate() throws Exception {
        ExportManager mockExportManager = new ExportManagerStub();
        FileUtil.createIfMissing(testPath);
        ExportCommand exportCommand = new ExportCommand(mockExportManager, testPath);

        String expectedMessage = ExportCommand.MESSAGE_SUCCESS_WITH_DUPLICATE_NAME;
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);

        CommandResult result = exportCommand.execute(model);
        assertEquals(expectedCommandResult, result);

        String expectedExportedContent = expected;
        assertEquals(expectedExportedContent, ((ExportManagerStub) mockExportManager).getExportedContent());
        Files.deleteIfExists(testPath);
    }

    @Test
    public void execute_exportFailure() throws Exception {

        ExportManager failingExportManager = new FailingExportManager();
        ExportCommand exportCommand = new ExportCommand(failingExportManager, testPath);

        String expectedMessage = ExportCommand.MESSAGE_FAILURE;
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        CommandResult result = exportCommand.execute(model);
        assertEquals(expectedCommandResult, result);
    }

    @Test
    public void equals() {
        ExportCommand exportFirstCommand = new ExportCommand(new ExportManagerStub(), testPath);
        ExportCommand exportSecondCommand = new ExportCommand(new ExportManagerStub(), testPath);

        // same object -> returns true
        assertTrue(exportFirstCommand.equals(exportFirstCommand));

        // different types -> returns false
        assertFalse(exportFirstCommand.equals(1));

        // null -> returns false
        assertFalse(exportFirstCommand.equals(null));

        // same person list
        exportFirstCommand.execute(model);
        exportSecondCommand.execute(model);
        assertTrue(exportFirstCommand.equals(exportSecondCommand));
    }

    @Test
    public void toStringMethod() {
        ExportCommand exportCommand = new ExportCommand(new ExportManagerStub(), testPath);
        String expected = ExportCommand.class.getCanonicalName() + "{Student list to export: =null}";
        assertEquals(expected, exportCommand.toString());
    }


    private class ExportManagerStub extends ExportManager {
        private String exportedContent = "";

        @Override
        public void exportStudentList(ObservableList<Person> studentList, Path pathToExportTo) throws IOException {
            StringBuilder csvContent = new StringBuilder();
            for (Person person : studentList) {
                csvContent.append(person.getStudentId().toString()).append(",");
                csvContent.append(person.getName().toString()).append("\n");
            }
            exportedContent = csvContent.toString();
        }

        public String getExportedContent() {
            return exportedContent;
        }

    }

    private class FailingExportManager extends ExportManager {
        @Override
        public void exportStudentList(ObservableList<Person> studentList, Path pathToExportTo) throws IOException {
            throw new IOException("Simulated export failure");
        }
    }

}
