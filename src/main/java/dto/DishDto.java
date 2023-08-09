package dto;


import com.qmkf.domain.Dish;
import com.qmkf.domain.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();
    
    private String categoryName;

    private Integer copies;
}
