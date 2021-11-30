package com.rohlik.useradminbe.services;

import com.rohlik.useradminbe.DTO.SearchFiltersDTO;
import com.rohlik.useradminbe.DTO.UserDTO;
import com.rohlik.useradminbe.DTO.UserDTO2;
import com.rohlik.useradminbe.models.User;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserService {
    private List<User> userData = new ArrayList<>();

    public boolean flipStatus(int userId) {
        Optional<User> targetUser = userData.stream().filter(user -> user.getId().equals(userId)).findFirst();
        if (targetUser.isPresent()) {
            if (targetUser.get().getStatus().equals("Active")) {
                targetUser.get().setStatus("Deactivated");
            } else {
                targetUser.get().setStatus("Active");
            }
            return true;
        }
        return false;
    }

    public boolean createUser(UserDTO userDTO) {
        int originalSize = userData.size();
        int newId;
        if (userData.size()>0) {
            newId = userData.get(userData.size()-1).getId()+1;
        } else {
            newId = 1;
        }
        userData.add(new User(
                newId,
                userDTO.getFirstName(),
                userDTO.getSurname(),
                userDTO.getStatus(),
                userDTO.getEmail(),
                userDTO.getTelephone(),
                new Date()
        ));

        return userData.size() > originalSize;
    }

    public boolean editUser(UserDTO2 userDto) {
        User targetUser = userData.stream().filter(user -> user.getId().equals(userDto.getId())).findFirst().get();

        User updatedUser = new User(
                targetUser.getId(),
                userDto.getFirstName(),
                userDto.getSurname(),
                userDto.getStatus(),
                userDto.getEmail(),
                userDto.getTelephone(),
                targetUser.getCreationDate()
        );

        userData.set(userData.indexOf(targetUser), updatedUser);

        return (!targetUser.getFirstName().equals(updatedUser.getFirstName()) ||
        !targetUser.getSurname().equals(updatedUser.getSurname()) ||
        !targetUser.getStatus().equals(updatedUser.getStatus()) ||
        !targetUser.getEmail().equals(updatedUser.getEmail()) ||
        targetUser.getTelephone() != updatedUser.getTelephone());
    }

    public boolean deleteUser(int userId) {
        int originalListSize = userData.size();
        userData = userData.stream().filter(user -> !user.getId().equals(userId)).collect(Collectors.toList());
        return originalListSize > userData.size();
    }

    public List<?> lazyLoadUsers(int offset, int rows, SearchFiltersDTO filters, String sortField, int sortOrder) {
        List<User> filteredUsers = filterUsers(filters);
        Integer totalRecords = filteredUsers.size();

        List<User> sortedUsers = sortUsers(filteredUsers, sortField, sortOrder);

        List<User> lazyLoadedUsers = new ArrayList<>();
        for (int i = offset; i < offset+rows; i++) {
            if (i < offset+rows && i < sortedUsers.size()) {
                    lazyLoadedUsers.add(sortedUsers.get(i));
            }
        }

        return Arrays.asList(
                lazyLoadedUsers, totalRecords
        );
    }

    public List<User> filterUsers(SearchFiltersDTO filters) {
        List<User> filterResults = new ArrayList<>();

        if (filters.id != null) {
            Optional<User> targetUser = userData.stream().filter(user -> user.getId().equals(filters.id)).findFirst();
            if (targetUser.isPresent()) {
                filterResults.add(targetUser.get());
            }
            return filterResults;
        }

        Iterator<User> iterator = userData.iterator();
        while(iterator.hasNext()) {
            filterResults.add((User) iterator.next().clone());
        }

        if(filters.firstName != null && !filters.firstName.equals("")) {
            filterResults = userData.stream().filter(user -> user.getFirstName().toLowerCase().contains(filters.firstName.toLowerCase())).collect(Collectors.toList());
        }

        if (filters.surname != null && !filters.surname.equals("")) {
            filterResults = filterResults.stream().filter(user -> user.getSurname().toLowerCase().contains(filters.surname.toLowerCase())).collect(Collectors.toList());
        }

        if (filters.creationFrom != null) {
            filterResults = filterResults.stream().filter(user -> user.getCreationDate().after(filters.creationFrom)).collect(Collectors.toList());
        }

        if (filters.creationTo != null) {
            filterResults = filterResults.stream().filter(user -> user.getCreationDate().before(filters.creationTo)).collect(Collectors.toList());
        }

        if (filters.status != null && !filters.status.equals("")) {
            filterResults = filterResults.stream().filter(user -> user.getStatus().equalsIgnoreCase(filters.status)).collect(Collectors.toList());
        }
        return filterResults;
    }

    public List<User> sortUsers(List<User> listToSort, String sortField, int sortOrder) {
        if (sortField != null) {
            switch (sortField) {
                case "id":
                    if (sortOrder == 1) {
                        listToSort.sort(new IdComparatorAsc());
                    } else {
                        listToSort.sort(new IdComparatorDesc());
                    }
                    break;
                case "firstName":
                    if (sortOrder == 1) {
                        listToSort.sort(new FirstNameComparatorAsc());
                    } else {
                        listToSort.sort(new FirstNameComparatorDesc());
                    }
                    break;
                case "surname":
                    if (sortOrder == 1) {
                        listToSort.sort(new SurnameComparatorAsc());
                    } else {
                        listToSort.sort(new SurnameComparatorDesc());
                    }
                    break;
                case "status":
                    if (sortOrder == 1) {
                        listToSort.sort(new StatusComparatorAsc());
                    } else {
                        listToSort.sort(new StatusComparatorDesc());
                    }
                    break;
                case "email":
                    if (sortOrder == 1 ) {
                        listToSort.sort(new EmailComparatorAsc());
                    } else {
                        listToSort.sort(new EmailComparatorDesc());
                    }
                    break;
                case "telephone":
                    if (sortOrder == 1 ) {
                        listToSort.sort(new TelephoneComparatorAsc());
                    } else {
                        listToSort.sort(new TelephoneComparatorDesc());
                    }
                    break;
                case "creationDate":
                    if (sortOrder == 1 ) {
                        listToSort.sort(new CreationDateComparatorAsc());
                    } else {
                        listToSort.sort(new CreationDateComparatorDesc());
                    }
                    break;
            }
        }
        return listToSort;
    }

    public void initDatabase() throws ParseException {
        List<String> firstNames = new ArrayList<>(
                Arrays.asList("Adam", "Bernard", "Cyril", "Dimitri", "Emil", "Filip", "Gordon", "Anna", "Barbora",
                        "Claudia", "Daniela", "Eliska", "Fiona", "Gabriela")
        );
        List<String> surnames = new ArrayList<>(
                Arrays.asList("Moreno", "Yan", "Volkanovski", "Oliveira", "Usman", "Adesanya", "Teixeira", "Ngannou",
                        "Namajunas", "Schevchenko", "Nunes")
        );
        String active;
        int telephone;
        Random random = new Random();

        for (String surname : surnames) {
            for (String firstName : firstNames) {
                if (random.nextInt(2) == 1) {
                    active = "Active";
                } else {
                    active = "Deactivated";
                }
                telephone = random.nextInt(876543211) + 123456789;
                userData.add(new User(userData.size() + 1,
                        firstName,
                        surname,
                        active,
                        firstName.toLowerCase() + '.' + surname.toLowerCase() + "@gmail.com",
                        telephone,
                        randomDate()));
            }
        }
    }

    public Date randomDate() {
        long startMillis = new Date(741900000000L).getTime();
        long endMillis = new Date(1637500000000L).getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
    }

    class IdComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getId() - u2.getId();
        }
    }
    class IdComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getId() - u1.getId();
        }
    }
    class FirstNameComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getFirstName().compareTo(u2.getFirstName());
        }
    }
    class FirstNameComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getFirstName().compareTo(u1.getFirstName());
        }
    }
    class SurnameComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getSurname().compareTo(u2.getSurname());
        }
    }
    class SurnameComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getSurname().compareTo(u1.getSurname());
        }
    }
    class StatusComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getStatus().compareTo(u2.getStatus());
        }
    }
    class StatusComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getStatus().compareTo(u1.getStatus());
        }
    }
    class EmailComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getEmail().compareTo(u2.getEmail());
        }
    }
    class EmailComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getEmail().compareTo(u1.getEmail());
        }
    }
    class TelephoneComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getTelephone() - u2.getTelephone();
        }
    }
    class TelephoneComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u2.getTelephone() - u1.getTelephone();
        }
    }
    class CreationDateComparatorAsc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            if (u1.getCreationDate().before(u2.getCreationDate())) {
                return -1;
            } else if (u1.getCreationDate().after(u2.getCreationDate())) {
                return 1;
            }
            return 0;
        }
    }
    class CreationDateComparatorDesc implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            if (u2.getCreationDate().before(u1.getCreationDate())) {
                return -1;
            } else if (u2.getCreationDate().after(u1.getCreationDate())) {
                return 1;
            }
            return 0;
        }
    }
}
