package deepend0.springcacheext.flatcacheable;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class TopClass {
    String id;
    List<MidClass> midClasses;

    public TopClass(String id) {
        this.id = id;
    }

    public TopClass(String id, List<MidClass> midClasses) {
        this.id = id;
        this.midClasses = midClasses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<MidClass> getMidClasses() {
        return midClasses;
    }

    public void setMidClasses(List<MidClass> midClasses) {
        this.midClasses = midClasses;
    }
}

class MidClass {
    String id;
    String name;
    List<LowClass> lowClasses;
    TopClass topClass;

    public MidClass(String id) {
        this.id = id;
    }
    public MidClass(String id, String name, List<LowClass> lowClasses) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LowClass> getLowClasses() {
        return lowClasses;
    }

    public void setLowClasses(List<LowClass> lowClasses) {
        this.lowClasses = lowClasses;
    }

    public TopClass getTopClass() {
        return topClass;
    }

    public void setTopClass(TopClass topClass) {
        this.topClass = topClass;
    }
}

class LowClass {
    String id;
    MidClass midClass;

    public LowClass(String id) {
        this.id = id;
    }

    public LowClass(String id, MidClass midClass) {
        this.id = id;
        this.midClass = midClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MidClass getMidClass() {
        return midClass;
    }

    public void setMidClass(MidClass midClass) {
        this.midClass = midClass;
    }
}
@Service
public class DataPopulatorService {
    private Map<String, TopClass> topClasses = new HashMap<>();
    private Map<String, MidClass> midClasses = new HashMap<>();
    private Map<String, LowClass> lowClasses = new HashMap<>();

    @PostConstruct
    public void init() {
        TopClass topClass1 = new TopClass("1");
        TopClass topClass2 = new TopClass("2");
        topClasses.put(topClass1.id, topClass1);
        topClasses.put(topClass2.id, topClass2);

        MidClass midClass1_1 = new MidClass("1_1");
        midClass1_1.topClass = topClass1;
        MidClass midClass1_2 = new MidClass("1_2");
        midClass1_2.topClass = topClass1;
        topClass1.midClasses = List.of(midClass1_1, midClass1_2);
        MidClass midClass2_1 = new MidClass("2_1");
        midClass2_1.topClass = topClass2;
        MidClass midClass2_2 = new MidClass("2_2");
        midClass2_2.topClass = topClass2;
        topClass2.midClasses = List.of(midClass2_1, midClass2_2);
        midClasses.put(midClass1_1.id, midClass1_1);
        midClasses.put(midClass1_2.id, midClass1_2);
        midClasses.put(midClass2_1.id, midClass2_1);
        midClasses.put(midClass2_2.id, midClass2_2);

        LowClass lowClass1_1_1 = new LowClass("1_1_1");
        lowClass1_1_1.midClass = midClass1_1;
        LowClass lowClass1_1_2 = new LowClass("1_1_2");
        lowClass1_1_2.midClass = midClass1_1;
        midClass1_1.lowClasses = List.of(lowClass1_1_1, lowClass1_1_2);
        LowClass lowClass1_2_1 = new LowClass("1_2_1");
        lowClass1_2_1.midClass = midClass1_2;
        LowClass lowClass1_2_2 = new LowClass("1_2_2");
        lowClass1_2_2.midClass = midClass1_2;
        midClass1_2.lowClasses = List.of(lowClass1_2_1, lowClass1_2_2);
        LowClass lowClass2_1_1 = new LowClass("2_1_1");
        lowClass2_1_1.midClass = midClass2_1;
        LowClass lowClass2_1_2 = new LowClass("2_1_2");
        lowClass2_1_2.midClass = midClass2_1;
        midClass2_1.lowClasses = List.of(lowClass2_1_1, lowClass2_1_2);
        LowClass lowClass2_2_1 = new LowClass("2_2_1");
        lowClass2_2_1.midClass = midClass2_2;
        LowClass lowClass2_2_2 = new LowClass("2_2_2");
        lowClass2_2_2.midClass = midClass2_2;
        midClass2_2.lowClasses = List.of(lowClass2_2_1, lowClass2_2_2);
        lowClasses.put(lowClass1_1_1.id, lowClass1_1_1);
        lowClasses.put(lowClass1_1_2.id, lowClass1_1_2);
        lowClasses.put(lowClass1_2_1.id, lowClass1_2_1);
        lowClasses.put(lowClass1_2_2.id, lowClass1_2_2);
        lowClasses.put(lowClass2_1_1.id, lowClass2_1_1);
        lowClasses.put(lowClass2_1_2.id, lowClass2_1_2);
        lowClasses.put(lowClass2_2_1.id, lowClass2_2_1);
        lowClasses.put(lowClass2_2_2.id, lowClass2_2_2);
    }

    public List<TopClass> findAllTopClasses() {
        return topClasses.values().stream().collect(Collectors.toList());
    }

    public List<TopClass> findTopClassesByIds(List<String> topClassIds) {
        return topClasses.values().stream().filter(topClass -> topClassIds.contains(topClass.id))
                .collect(Collectors.toList());
    }

    public List<TopClass> findTopClassesByLowClassIds(List<String> lowClassIds) {
        return topClasses.values().stream()
                .flatMap(tc->tc.midClasses.stream().map(mc->new AbstractMap.SimpleEntry<>(tc, mc)))
                .flatMap(tc_mc->tc_mc.getValue().lowClasses.stream().map(lc->new AbstractMap.SimpleEntry<>(tc_mc.getKey(), lc)))
                .filter(tc_lc->lowClassIds.contains(tc_lc.getValue().id))
                .collect(Collectors.groupingBy(tc_lc->tc_lc.getKey())).keySet().stream().collect(Collectors.toList());
    }

    public List<LowClass> findAllLowClasses() {
        return lowClasses.values().stream().collect(Collectors.toList());
    }

    public List<LowClass> findLowClassesByIds(List<String> lowClassIds) {
        return lowClasses.values().stream().filter(lowClass -> lowClassIds.contains(lowClass.id))
                .collect(Collectors.toList());
    }

    public List<LowClass> findLowClassesByTopClassIds(List<String> topClassIds) {
        return lowClasses.values().stream()
                .map(lc->new AbstractMap.SimpleEntry<>(lc.midClass.topClass, lc))
                .filter(tc_lc->topClassIds.contains(tc_lc.getKey().id))
                .map(tc_lc->tc_lc.getValue())
                .collect(Collectors.toList());
    }
}