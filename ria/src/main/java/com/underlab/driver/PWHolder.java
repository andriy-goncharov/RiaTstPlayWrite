package com.underlab.driver;

import com.microsoft.playwright.*;



public class PWHolder {
    private ThreadLocal<Playwright> playwright = new ThreadLocal<>();
    private ThreadLocal<BrowserContext> context = new ThreadLocal<>();
    private ThreadLocal<Page> page = new ThreadLocal<>();

    private static PWHolder instance = null;

    private PWHolder() {
    }

    public synchronized static PWHolder getInstance() {
        if (instance == null) {
            instance = new PWHolder();
        }
        return instance;
    }

    public Playwright getPlaywright() {
        if (playwright.get() == null) {
            playwright.set(Playwright.create());
        }
        return playwright.get();
    }

    public BrowserContext getContext() {
        if (context.get() == null) {
            Boolean isHeadless = Boolean
                    .parseBoolean(System.getProperty("tests.headless", "false"));
            BrowserContext browserContext = getBrowser()
                    .launch(new BrowserType.LaunchOptions().setHeadless(isHeadless)).newContext();
            context.set(browserContext);
        }
        return context.get();
    }

    public Page getPage(){
        if (page.get() == null){
            page.set(getContext().newPage());
        }
        return page.get();
    }

    private BrowserType getBrowser() {
        String browser = System.getProperty("tests.browser", "chromium");
        return switch (browser) {
            case "chromium" -> getPlaywright().chromium();
            case "firefox" -> getPlaywright().firefox();
            case "webkit" -> getPlaywright().webkit();
            default -> throw new IllegalArgumentException(browser +
                    " is not supported. Please use [chromium, firefox, webkit]");
        };
    }
}
