package ro.iss.service;

import ro.iss.domain.Drug;
import ro.iss.repository.DrugRepository;

import java.util.List;

public class DrugService {
    private final DrugRepository drugRepository = new DrugRepository();

    public void addDrug(String name, String description) {
        Drug drug = new Drug();
        drug.setName(name);
        drug.setDescription(description);

        drugRepository.save(drug);
    }

    public List<Drug> getAllDrugs() {
        return drugRepository.findAll();
    }

    public Drug getDrugById(int id) {
        return drugRepository.findById(id);
    }
}
