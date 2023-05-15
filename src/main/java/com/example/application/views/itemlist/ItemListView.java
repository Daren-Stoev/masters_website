package com.example.application.views.itemlist;

import com.example.application.data.entity.Item;
import com.example.application.data.entity.Users;
import com.example.application.data.service.ItemService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.util.List;
import java.util.Optional;
@PageTitle("Item List")
@Route(value = "item-list/:ItemID?/:action?(edit)", layout = MainLayout.class)
@AnonymousAllowed
public class ItemListView extends Main implements HasComponents, HasStyle, BeforeEnterObserver {

    private final String ITEM_ID = "ItemID";
    private TextField searchBar;
    private Checkbox filter;
    private final ItemService itemService;
    private final Button createButton;
    private List<Item> items;
    private OrderedList itemContainer;

    public ItemListView(ItemService itemService) {
        this.itemService = itemService;
        constructUI();
        items = itemService.findAllItems();
        filter = new Checkbox("Show only your items");
        //filter.addClickListener(e -> {});
        searchBar = new TextField("Type here to search");
        searchBar.addValueChangeListener(e -> {
            items = itemService.findByNameStartsWithIgnoreCase(searchBar.getValue());
            itemContainer.removeAll();
            for ( Item item : items) {
                if(filter.getValue() && item.getUser() == VaadinSession.getCurrent().getAttribute(Users.class)){
                    itemContainer.add(new ItemListViewCard(item));
                } else if (!filter.getValue())
                {
                    itemContainer.add(new ItemListViewCard(item));
                }
            }
        });
        createButton = new Button("Add item");
        createButton.addClickListener(e ->
        {
            UI.getCurrent().navigate(ItemNewView.class);
        });
        add(searchBar,filter,createButton);
        if (items != null){
            for ( Item item : items) {
                itemContainer.add(new ItemListViewCard(item));
            }}
    }

    private void constructUI() {
        addClassNames("image-list-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Beautiful photos");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Royalty free photos and pictures, courtesy of Unsplash");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortBy = new Select<>();
        sortBy.setLabel("Sort by");
        sortBy.setItems("Popularity", "Newest first", "Oldest first");
        sortBy.setValue("Popularity");

        itemContainer = new OrderedList();
        itemContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        container.add(headerContainer, sortBy);
        add(container, itemContainer);

    }
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
      /*  Optional<Long> ItemId = event.getRouteParameters().get(ITEM_ID).map(Long::parseLong);
        if (ItemId.isPresent()) {
            Optional<Item> ItemFromBackend = itemService.get(ItemId.get());
            if (ItemFromBackend.isPresent()) {
                items.add(ItemFromBackend.get());
            }
        }*/
    }
}
