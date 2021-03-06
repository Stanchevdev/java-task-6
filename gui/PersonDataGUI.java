package fmi.informatics.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fmi.informatics.comparators.AgeComparator;
import fmi.informatics.comparators.EgnComparator;
import fmi.informatics.comparators.HeightComparator;
import fmi.informatics.comparators.NameComparator;
import fmi.informatics.comparators.PersonComparator;
import fmi.informatics.comparators.WeightComparator;
import fmi.informatics.enums.EType;
import fmi.informatics.extending.Person;
import fmi.informatics.extending.Professor;
import fmi.informatics.extending.Student;
import fmi.informatics.util.FileReader;

import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

// създаваме клас PersonDataGUI
public class PersonDataGUI {

	public static Person[] people;
	public static Person[] peopleBackUp;
	JTable table;
	public static PersonDataModel personDataModel;
	public static PersonDataModel personDataModelBackup;

	public static void main(String[] args) {
		// Ако извикваме четенето от файл, трябва да закоментираме метода getPeople()
		getPeople();
		// TODO Извикваме четенето от файл
		// people = FileReader.readPeople();

		// TODO Извикваме писането във файл
		initializeData();

		PersonDataGUI gui = new PersonDataGUI();
		gui.createAndShowGUI();
	}

	// TODO Добавяме писането/четенето във файл
	private static void initializeData() {
		if (!FileReader.isFileExists()) {
			FileReader.createPersonFile();
		}

		FileReader.writePeople(people);
	}

	public static void getPeople() {
		people = new Person[8];
		peopleBackUp = new Person[8];
		for (int i = 0; i < 4; i++) {
			Person student = Student.StudentGenerator.make();
			people[i] = student;
			peopleBackUp[i] = student;
		}

		for (int i = 4; i < 8; i++) {
			Person professor = Professor.ProfessorGenerator.make();
			people[i] = professor;
			peopleBackUp[i] = professor;
		}

		personDataModelBackup = new PersonDataModel(peopleBackUp);

	}

	public void createAndShowGUI() {
		JFrame frame = new JFrame("Таблица с данни за хора");
		frame.setSize(500, 500);

		JLabel label = new JLabel("Списък с потребители", JLabel.CENTER);
		personDataModel = new PersonDataModel(people);
		table = new JTable(personDataModel);
		JScrollPane scrollPane = new JScrollPane(table);
		// Добавяме бутон за сортиране по години с Comparable interface
		JButton buttonReset = new JButton("Рестартирай");
		JButton buttonSortAge = new JButton("Сортирай по години");

		// Добавяме бутон за сортиране
		JButton buttonSort = new JButton("Сортирай");

		// TODO Добавяме бутон за филтриране
		JButton buttonFilter = new JButton("Филтрирай");

		// TODO Добавяме панел, където ще поставим бутоните
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(buttonReset);
		buttonsPanel.add(buttonSortAge);
		buttonsPanel.add(buttonSort);
		buttonsPanel.add(buttonFilter);

		Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(label, BorderLayout.NORTH);
		container.add(scrollPane, BorderLayout.CENTER);
		// TODO Добавяме панелът с бутоните в контейнера
		container.add(buttonsPanel, BorderLayout.SOUTH);


		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				personDataModel = personDataModelBackup;
				table.setModel(personDataModel);
				table.repaint();

			}
		});

		// Добавяме listener към бутона за сортиране по години
		buttonSortAge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Arrays.sort(people);
				table.repaint();
			}
		});

		// TODO Променяме диалога за сортиране
		final JDialog sortDialog = new CustomDialog(getSortText(), this, EType.SORT);

		// TODO Добавяме диалог за филтрацията
		final JDialog filterDialog = new CustomDialog(getFilterText(), this, EType.FILTER);

		// Добавяме listener към бутона за сортиране
		buttonSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sortDialog.pack();
				sortDialog.setVisible(true);
			}
		});

		// TODO Добавяме listener за филтрация
		buttonFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				filterDialog.pack();
				filterDialog.setVisible(true);
			}
		});

		frame.setVisible(true);
		table.addMouseListener(new InfoClass());
	}

	// TODO Добавяме метод за филтриране
	public void filterTable(int intValue, JTable table, Person[] people) {

		switch (intValue) {
			case 1:
				this.personDataModel = new PersonDataModel(filterData(people, Student.class));
				break;
			case 2:
				this.personDataModel = new PersonDataModel(filterData(people, Professor.class));
				break;
			case 3:
				this.personDataModel = new PersonDataModel(filterData(people, Person.class));
				break;
		}

		table.setModel(this.personDataModel);
		table.repaint();
	}

	// TODO Добавяме помощен метод за филтриране
	private Person[] filterData(Person[] persons, Class<?> clazz) {
		ArrayList<Person> filteredData = new ArrayList<>();

		for (int i = 0; i < persons.length; i++) {

			if (clazz == Person.class) {
				// Тук обхващаме филтрирането на "Други"
				if (!(persons[i] instanceof Student) && !(persons[i] instanceof Professor)) {
					filteredData.add(persons[i]);
				}
			} else if (clazz.isInstance(persons[i])) { // Филтриране по студент или професор
				filteredData.add(persons[i]);
			}
		}

		// Преобразуваме списъка в масив
		Person[] filteredDataArray = new Person[filteredData.size()];
		filteredDataArray = filteredData.toArray(filteredDataArray);
		return filteredDataArray;
	}

	public void sortTable(int intValue, JTable table, Person[] people) {
		PersonComparator comparator = null;

		switch (intValue) {
			case 1:
				comparator = new NameComparator();
				break;
			case 2:
				comparator = new EgnComparator();
				break;
			case 3:
				comparator = new HeightComparator();
				break;
			case 4:
				comparator = new WeightComparator();
				break;
			case 5:
				comparator = new AgeComparator();
				break;
		}

		if (comparator == null) { // Ако стойността е null - сортирай по подразбиране
			Arrays.sort(people); // Сортировка по подразбиране по години
		} else {
			Arrays.sort(people, comparator);
		}

		table.repaint();
	}

	private static String getSortText() {
		return "Моля, въведете цифрата на колоната, по която да се сортират данните: \n" +
				" 1 - Име \n" +
				" 2 - ЕГН \n" +
				" 3 - Височина \n" +
				" 4 - Тегло \n" +
				" 5 - Години \n";
	}

	// TODO Добавяме текст, който да се визуализира в диалога за филтриране
	private static String getFilterText() {
		return "Моля, въведете цифрата на филтъра, който искате да използвате: \n" +
				" 1 - Студенти \n" +
				" 2 - Преподаватели \n" +
				" 3 - Други \n";
	}


	private class InfoClass implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() > 1) {
				StringBuilder infoString = new StringBuilder();
				infoString.append(" Име: ");
				infoString.append(table.getValueAt(table.getSelectedRow(), 0).toString());
				infoString.append(", Егн: ");
				infoString.append(table.getValueAt(table.getSelectedRow(), 1).toString());
				infoString.append(" Възраст: ");
				infoString.append(table.getValueAt(table.getSelectedRow(), 2).toString());
				infoString.append(", Височина: ");
				infoString.append(table.getValueAt(table.getSelectedRow(), 3).toString());
				infoString.append(" Тегло: ");
				infoString.append(table.getValueAt(table.getSelectedRow(), 4).toString());

				final JDialog infoDialog = new CustomDialog(infoString);
				infoDialog.pack();
				infoDialog.setVisible(true);
			}

		}

		@Override
		public void mousePressed(MouseEvent mouseEvent) {

		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent) {

		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {

		}

		@Override
		public void mouseExited(MouseEvent mouseEvent) {

		}
	}
}