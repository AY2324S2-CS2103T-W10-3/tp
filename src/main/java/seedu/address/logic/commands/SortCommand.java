package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.logic.Messages;
import java.util.Comparator;

import javax.xml.namespace.QName;

public class SortCommand extends Command{

    public static final String COMMAND_WORD = "sort";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sorts the list of persons in the address book by the specified field.\n"
            + "Parameters: FIELD\n"
            + "Example: " + COMMAND_WORD + " name";
    
    public static final Comparator<Person> PERSON_NAME_COMPARATOR = Comparator.comparing( person -> person.getNameAsString(), new NameAlphabeticalComparator());
    public static final Comparator<Person> PERSON_ID_COMPARATOR = Comparator.comparingInt(Person::getStudentIdAsInt);
    public String sortType;

    public static class NameAlphabeticalComparator implements Comparator<String> {
        @Override
        public int compare(String name1, String name2) {
            int result = name1.compareTo(name2); // Compare alphabetically

            // If the names are the same, break ties by comparing next alphabet
            if (result == 0) {
                int minLength = Math.min(name1.length(), name2.length());
                for (int i = 0; i < minLength; i++) {
                    if (name1.charAt(i) != name2.charAt(i)) {
                        return name1.charAt(i) - name2.charAt(i);
                    }
                }
                // If all characters are the same up to the length of the shorter string,
                // the shorter string comes first.
                return name1.length() - name2.length();
            }
            return result;
        }
    }


    public SortCommand(String sortType) {
        this.sortType = sortType;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        switch (sortType) {
            case "id":
                model.sortFilteredPersonList(PERSON_ID_COMPARATOR);
                return new CommandResult(String.format(Messages.MESSAGE_LIST_SORTED_SUCCESSFULLY, "id"));
            case "name":
                model.sortFilteredPersonList(PERSON_NAME_COMPARATOR );
                return new CommandResult(String.format(Messages.MESSAGE_LIST_SORTED_SUCCESSFULLY, "name"));
            default:
                throw new CommandException(Messages.MESSAGE_INVALID_COMMAND_FORMAT);
        }
    }

    
}
