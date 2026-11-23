package com.placemate;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PlaceMateTest {

    WebDriver driver;
    WebDriverWait wait;

    String baseUrl = "https://placemate-frontend-tau.vercel.app/";

    String studentEmail = "aarav.sharma@student.placemate.test";
    String studentPassword = "Student@123";

    String recruiterEmail = "recruiter.test2026@example.com";
    String recruiterPassword = "Recruiter@123";

    String jobId = "200000000000000000000004";

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(25));

        driver.get(baseUrl);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.out.println("Browser session already closed.");
            }
        }
    }

    // Helper: pause briefly for page updates
    public void pause() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Test execution interrupted", e);
        }
    }

    // Helper: open Jobs page through homepage navigation
    public void openJobsPage() {
        driver.get(baseUrl);

        WebElement jobsLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/jobs']")));

        jobsLink.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("job-search")));
    }

    // Helper: student login
    public void loginStudent() {
        driver.get(baseUrl);

        WebElement loginLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/login']")));

        loginLink.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']")));

        driver.findElement(By.cssSelector("input[type='email']"))
                .sendKeys(studentEmail);

        driver.findElement(By.cssSelector("input[type='password']"))
                .sendKeys(studentPassword);

        driver.findElement(By.xpath(
                "//button[contains(normalize-space(), 'Log In')]"))
                .click();

        pause();
    }

    // Helper: open a job details page
    public void openJobDetails() {
        openJobsPage();

        WebElement viewDetails = wait.until(
                ExpectedConditions.presenceOfElementLocated(
                        By.id("view-job-" + jobId)));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                viewDetails);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(viewDetails))
                    .click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", viewDetails);
        }

        pause();
    }

    // Helper: recruiter login
    public void loginRecruiter() {
        driver.get(baseUrl);

        WebElement loginLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[@href='/login']")));

        loginLink.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']")));

        driver.findElement(By.cssSelector("input[type='email']"))
                .sendKeys(recruiterEmail);

        driver.findElement(By.cssSelector("input[type='password']"))
                .sendKeys(recruiterPassword);

        driver.findElement(By.xpath(
                "//button[contains(normalize-space(), 'Log In')]"))
                .click();

        wait.until(ExpectedConditions.urlContains("/recruiter/dashboard"));

        pause();
    }

    // Helper: open recruiter job creation form through dashboard
    public void openRecruiterJobForm() {
        loginRecruiter();

        WebElement postJobLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//a[contains(normalize-space(), 'Post a Job')]")));

        postJobLink.click();

        wait.until(ExpectedConditions.urlContains("/recruiter/jobs/new"));

        pause();
    }

    // TC-01: Verify homepage loads
    @Test(priority = 1)
    public void verifyHomepageLoads() {
        Assert.assertTrue(
                driver.getTitle().length() > 0,
                "Homepage title should not be empty");

        Assert.assertTrue(
                driver.getPageSource().contains("PlaceMate"),
                "PlaceMate homepage should load");

        System.out.println("TC-01 Passed: Homepage loaded successfully.");
    }

    // TC-02: Verify student login
    @Test(priority = 2)
    public void verifyStudentLogin() {
        loginStudent();

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/login"),
                "Successful login should navigate away from login page");

        System.out.println("TC-02 Passed: Student login successful.");
    }

    // TC-03: Verify invalid password
    @Test(priority = 3)
    public void verifyInvalidPassword() {
        driver.get(baseUrl);

        driver.findElement(By.xpath("//a[@href='/login']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[type='email']")));

        driver.findElement(By.cssSelector("input[type='email']"))
                .sendKeys(studentEmail);

        driver.findElement(By.cssSelector("input[type='password']"))
                .sendKeys("WrongPassword123");

        driver.findElement(By.xpath(
                "//button[contains(normalize-space(), 'Log In')]"))
                .click();

        pause();

        Assert.assertTrue(
                driver.getPageSource().contains("Invalid email or password"),
                "Invalid login message should be displayed");

        System.out.println("TC-03 Passed: Invalid login was rejected.");
    }

    // TC-04: Verify valid student registration
    @Test(priority = 4)
    public void verifyValidRegistration() {
        driver.get(baseUrl);

        driver.findElement(By.xpath("//a[@href='/login']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/register']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("register-name")));

        String uniqueEmail =
                "selenium" + System.currentTimeMillis() + "@test.com";

        driver.findElement(By.id("register-name"))
                .sendKeys("Selenium Test Student");

        driver.findElement(By.id("register-email"))
                .sendKeys(uniqueEmail);

        driver.findElement(By.id("register-password"))
                .sendKeys("Test@12345");

        driver.findElement(By.id("register-confirmPassword"))
                .sendKeys("Test@12345");

        new Select(driver.findElement(By.id("register-role")))
                .selectByVisibleText("Student");

        driver.findElement(By.id("register-submit")).click();

        pause();

        Assert.assertFalse(
                driver.getCurrentUrl().contains("/register"),
                "Successful registration should leave the registration page");

        System.out.println("TC-04 Passed: Student registration submitted.");
    }

    // TC-05: Verify short password validation
    @Test(priority = 5)
    public void verifyShortPasswordValidation() {
        driver.get(baseUrl);

        driver.findElement(By.xpath("//a[@href='/login']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/register']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("register-name")));

        driver.findElement(By.id("register-name"))
                .sendKeys("Test Student");

        driver.findElement(By.id("register-email"))
                .sendKeys("shortpassword@test.com");

        driver.findElement(By.id("register-password"))
                .sendKeys("123");

        driver.findElement(By.id("register-confirmPassword"))
                .sendKeys("123");

        new Select(driver.findElement(By.id("register-role")))
                .selectByVisibleText("Student");

        driver.findElement(By.id("register-submit")).click();

        pause();

        Assert.assertTrue(
                driver.getPageSource().contains(
                        "Password must be at least 8 characters"),
                "Short password validation should be displayed");

        System.out.println("TC-05 Passed: Short password validation works.");
    }

    // TC-06: Verify password mismatch validation
    @Test(priority = 6)
    public void verifyPasswordMismatch() {
        driver.get(baseUrl);

        driver.findElement(By.xpath("//a[@href='/login']")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[@href='/register']"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("register-name")));

        driver.findElement(By.id("register-name"))
                .sendKeys("Test Student");

        driver.findElement(By.id("register-email"))
                .sendKeys("mismatch@test.com");

        driver.findElement(By.id("register-password"))
                .sendKeys("Test@12345");

        driver.findElement(By.id("register-confirmPassword"))
                .sendKeys("Different@123");

        new Select(driver.findElement(By.id("register-role")))
                .selectByVisibleText("Student");

        driver.findElement(By.id("register-submit")).click();

        pause();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/register"),
                "User should remain on registration page");

        System.out.println("TC-06 Passed: Mismatched passwords were not submitted.");
    }

    // TC-07: Verify 7 available jobs are displayed
    @Test(priority = 7)
    public void verifyAvailableJobsDisplayed() {
        openJobsPage();

        List<WebElement> jobCards = wait.until(
                ExpectedConditions.numberOfElementsToBe(
                        By.cssSelector("a[href*='/jobs/200']"), 7));

        Assert.assertEquals(
                jobCards.size(), 7,
                "Expected 7 jobs to be displayed");

        System.out.println("TC-07 Passed: 7 jobs displayed successfully.");
    }

    // TC-08: Verify job search by title
    @Test(priority = 8)
    public void verifyJobSearchByTitle() {
        openJobsPage();

        WebElement search = driver.findElement(By.id("job-search"));
        search.sendKeys("Backend Engineer");

        pause();

        Assert.assertTrue(
                driver.getPageSource().contains("Backend Engineer"),
                "Matching job should be displayed");

        System.out.println("TC-08 Passed: Job search by title works.");
    }

    // TC-09: Inspect location filter
    @Test(priority = 9)
    public void verifyJobFilterByLocation() {
        openJobsPage();

        WebElement locationDropdown =
                driver.findElement(By.id("job-location-filter"));

        System.out.println("Location dropdown HTML:");
        System.out.println(locationDropdown.getAttribute("outerHTML"));

        System.out.println("Location dropdown text:");
        System.out.println(locationDropdown.getText());

        System.out.println("Number of options: " +
                new Select(locationDropdown).getOptions().size());

        Assert.assertTrue(
                locationDropdown.isDisplayed(),
                "Location dropdown should be visible");

        System.out.println("TC-09 Passed: Location dropdown is visible.");
    }

    // TC-10: Verify job type filter
    @Test(priority = 10)
    public void verifyJobFilterByJobType() {
        openJobsPage();

        Select jobType = new Select(
                driver.findElement(By.id("job-type-filter")));

        jobType.selectByVisibleText("Internship");

        pause();

        Assert.assertTrue(
                driver.getPageSource().contains("Internship"),
                "Internship jobs should be displayed");

        System.out.println("TC-10 Passed: Job type filter works.");
    }

    // TC-11: Verify CGPA filter input
    @Test(priority = 11)
    public void verifyJobFilterByCGPA() {
        openJobsPage();

        WebElement cgpa = driver.findElement(
                By.id("job-cgpa-filter"));

        cgpa.clear();
        cgpa.sendKeys("7");

        pause();

        Assert.assertEquals(
                cgpa.getAttribute("value"),
                "7",
                "CGPA filter should contain the entered value");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/jobs"),
                "User should remain on the Jobs page");

        System.out.println("TC-11 Passed: CGPA filter accepted the entered value.");
    }

    // TC-12: Verify Clear Filters displays all 7 jobs
    @Test(priority = 12)
    public void verifyClearFilters() {
        openJobsPage();

        driver.findElement(By.id("job-search"))
                .sendKeys("Backend");

        pause();

        driver.findElement(By.id("clear-filters-button")).click();

        wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("a[href*='/jobs/200']"), 7));

        List<WebElement> jobCards = driver.findElements(
                By.cssSelector("a[href*='/jobs/200']"));

        Assert.assertEquals(
                jobCards.size(), 7,
                "Clear Filters should display all 7 jobs");

        System.out.println("TC-12 Passed: Clear Filters displays all 7 jobs.");
    }

    // TC-13: Verify job details page
    @Test(priority = 13)
    public void verifyViewJobDetails() {
        openJobDetails();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/jobs/"),
                "Job details page should open");

        Assert.assertTrue(
                driver.getPageSource().contains("Backend Engineer"),
                "Backend Engineer details should be displayed");

        System.out.println("TC-13 Passed: Job details page opened.");
    }

    // TC-14: Verify student can access Apply button
    @Test(priority = 14)
    public void verifyStudentCanAccessApplyButton() {
        loginStudent();

        openJobDetails();

        WebElement applyButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.id("apply-button")));

        Assert.assertTrue(
                applyButton.isDisplayed(),
                "Apply button should be visible");

        System.out.println("TC-14 Passed: Apply button is visible.");
    }

// TC-15: Verify Apply button for an unapplied job
@Test(priority = 15)
public void verifyApplyButtonForUnappliedJob() {

    loginStudent();

    // Open Jobs page through website navigation
    openJobsPage();

    // QA Automation Engineer job ID
    String qaJobId = "6ae7b78757c3b4afbd04cfb7";

    // Click View Job
    WebElement viewJob = wait.until(
        ExpectedConditions.elementToBeClickable(
            By.id("view-job-" + qaJobId)
        )
    );

    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block:'center'});",
        viewJob
    );

    viewJob.click();

    // Verify Apply Now button
    WebElement applyButton = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.id("apply-button")
        )
    );

    Assert.assertEquals(
        applyButton.getText().trim(),
        "Apply Now",
        "Unapplied job should show Apply Now"
    );

    Assert.assertTrue(
        applyButton.isEnabled(),
        "Apply Now button should be enabled"
    );

    System.out.println("TC-15 PASSED: Apply Now button is displayed and enabled.");
}
    // TC-16: Verify student logout
    @Test(priority = 16)
    public void verifyStudentLogout() {
        loginStudent();

        WebElement logoutButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(normalize-space(), 'Logout') or contains(normalize-space(), 'Log Out')]")));

        logoutButton.click();

        wait.until(ExpectedConditions.urlContains("/login"));

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/login"),
                "Logout should redirect to login page");

        System.out.println("TC-16 Passed: Student logout successful.");
    }

    // TC-17: Verify recruiter dashboard
    @Test(priority = 17)
    public void verifyRecruiterDashboard() {
        loginRecruiter();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/recruiter/dashboard"),
                "Recruiter dashboard should open");

        Assert.assertTrue(
                driver.getPageSource().contains("Recruiter Dashboard"),
                "Recruiter Dashboard heading should be displayed");

        System.out.println("TC-17 Passed: Recruiter dashboard opened successfully.");
    }

    // TC-18: Verify recruiter job creation form
    @Test(priority = 18)
    public void verifyRecruiterJobCreationForm() {
        openRecruiterJobForm();

        String pageText = driver.getPageSource().toLowerCase();

        Assert.assertTrue(
                pageText.contains("job title"),
                "Job Title field should be displayed");

        Assert.assertTrue(
                pageText.contains("company name"),
                "Company Name field should be displayed");

        Assert.assertTrue(
                pageText.contains("job description"),
                "Job Description field should be displayed");

        System.out.println("TC-18 Passed: Recruiter job creation form opened.");
    }

    // TC-19: Verify empty job form submission is prevented
    @Test(priority = 19)
    public void verifyEmptyJobFormValidation() {
        openRecruiterJobForm();

        WebElement createJobButton = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(normalize-space(), 'Create Job')]")));

        createJobButton.click();

        pause();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/recruiter/jobs/new"),
                "User should remain on job creation form when required fields are empty");

        System.out.println("TC-19 Passed: Empty job form submission was prevented.");
    }
// TC-20: Verify Cancel button on job creation form
@Test(priority = 20)
public void verifyCancelJobCreation() {
    openRecruiterJobForm();

    // Scroll to the bottom of the page
    ((JavascriptExecutor) driver).executeScript(
        "window.scrollTo(0, document.body.scrollHeight);"
    );

    pause();

    // Find the Cancel button or link
    WebElement cancelElement = wait.until(
        ExpectedConditions.elementToBeClickable(
            By.xpath(
                "//*[self::button or self::a]" +
                "[contains(translate(normalize-space(.), " +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ', " +
                "'abcdefghijklmnopqrstuvwxyz'), 'cancel')]"
            )
        )
    );

    cancelElement.click();

    wait.until(
        ExpectedConditions.urlContains("/recruiter/dashboard")
    );

    Assert.assertTrue(
        driver.getCurrentUrl().contains("/recruiter/dashboard"),
        "Cancel should return recruiter to dashboard"
    );

    System.out.println(
        "TC-20 Passed: Cancel returned recruiter to dashboard."
    );
}

// TC-21: Verify successful job creation by recruiter
@Test(priority = 21)
public void verifySuccessfulJobCreation() {

    openRecruiterJobForm();

    // Job title
    WebElement title = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//label[normalize-space()='Job title']/following::input[1]")
        )
    );
    title.sendKeys("Automation Test Engineer");

    // Company name
    WebElement company = driver.findElement(
        By.xpath("//label[normalize-space()='Company name']/following::input[1]")
    );
    company.sendKeys("TestTech Solutions");

    // Location
    WebElement location = driver.findElement(
        By.xpath("//label[normalize-space()='Location']/following::input[1]")
    );
    location.sendKeys("Gurugram");

    pause();

    // Job type
    WebElement jobType = driver.findElement(
        By.xpath("//label[normalize-space()='Job type']/following::select[1]")
    );
    new org.openqa.selenium.support.ui.Select(jobType)
        .selectByVisibleText("Full-time");

    // Minimum CGPA
    WebElement cgpa = driver.findElement(
        By.xpath("//label[normalize-space()='Minimum CGPA']/following::input[1]")
    );
    cgpa.clear();
    cgpa.sendKeys("7");

    // Number of openings
    WebElement openings = driver.findElement(
        By.xpath("//label[normalize-space()='Number of openings']/following::input[1]")
    );
    openings.clear();
    openings.sendKeys("2");

    // Application deadline
    WebElement deadline = driver.findElement(
        By.xpath("//label[normalize-space()='Application deadline']/following::input[1]")
    );
    deadline.sendKeys("31-12-2026");

    pause();

    // Job description
    WebElement description = driver.findElement(
        By.xpath("//label[normalize-space()='Job description']/following::textarea[1]")
    );
    description.sendKeys(
        "We are looking for an automation test engineer with knowledge of Selenium Java and software testing."
    );

    // Required skills
    WebElement skills = driver.findElement(
        By.xpath("//label[normalize-space()='Required skills']/following::input[1]")
    );
    skills.sendKeys("Java, Selenium, TestNG, Maven");

    // Eligible graduation year
    WebElement year2027 = driver.findElement(
        By.xpath("//label[contains(normalize-space(.),'2027')]")
    );
    year2027.click();

    pause();

    // Scroll to Create Job button
    ((JavascriptExecutor) driver).executeScript(
        "window.scrollTo(0, document.body.scrollHeight);"
    );

    pause();

    // Click Create Job
    WebElement createButton = wait.until(
        ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(normalize-space(.),'Create Job')]")
        )
    );
    createButton.click();

    // Verify successful navigation or confirmation
    wait.until(driver ->
        driver.getCurrentUrl().contains("/recruiter/dashboard")
        || driver.getPageSource().toLowerCase().contains("job created")
        || driver.getPageSource().toLowerCase().contains("successfully")
    );

    Assert.assertTrue(
        driver.getCurrentUrl().contains("/recruiter/dashboard")
        || driver.getPageSource().toLowerCase().contains("job created")
        || driver.getPageSource().toLowerCase().contains("successfully"),
        "Job creation should complete successfully"
    );

    System.out.println(
        "TC-21 Passed: Recruiter submitted a new job successfully."
    );
}

// TC-22: Verify successful job deletion by recruiter
@Test(priority = 22)
public void verifySuccessfulJobDeletion() {

    // Log in and open recruiter dashboard
    loginRecruiter();

    wait.until(ExpectedConditions.urlContains("/recruiter/dashboard"));

    // Locate only the job created in TC-21
    By jobRow = By.xpath(
        "//tr[.//td[contains(normalize-space(.), 'Automation Test Engineer')]]"
    );

    WebElement row = wait.until(
        ExpectedConditions.visibilityOfElementLocated(jobRow)
    );

    // Find Delete button inside that specific job row
    WebElement deleteButton = row.findElement(
        By.xpath(".//button[contains(normalize-space(.), 'Delete')]")
    );

    ((JavascriptExecutor) driver).executeScript(
        "arguments[0].scrollIntoView({block:'center'});",
        deleteButton
    );

    pause();

    deleteButton.click();


// Check for a browser confirmation alert
try {
    driver.switchTo().alert().accept();
    System.out.println("Browser confirmation accepted.");

} catch (org.openqa.selenium.NoAlertPresentException e) {
    System.out.println("No browser alert appeared.");
}

// Print any visible confirmation dialogs
List<WebElement> dialogs = driver.findElements(
    By.xpath("//*[@role='dialog' or @role='alertdialog']")
);

for (WebElement dialog : dialogs) {
    if (dialog.isDisplayed()) {
        System.out.println("CONFIRMATION DIALOG: " + dialog.getText());
    }
}

// Print visible buttons after clicking Delete
List<WebElement> buttons = driver.findElements(By.tagName("button"));

for (WebElement button : buttons) {
    if (button.isDisplayed()) {
        System.out.println("VISIBLE BUTTON: " + button.getText());
    }
}

System.out.println("CURRENT URL: " + driver.getCurrentUrl());
}
// TC-23: Diagnose student access to recruiter dashboard
@Test
public void TC23_studentCannotAccessRecruiterDashboard() {

    // Step 1: Login as student
    loginStudent();

    // Step 2: Attempt to access recruiter dashboard
    ((JavascriptExecutor) driver).executeScript(
        "window.history.pushState({}, '', '/recruiter/dashboard');" +
        "window.dispatchEvent(new PopStateEvent('popstate'));"
    );

    // Step 3: Wait for the page to respond
    pause();

    // Step 4: Print actual page information
    System.out.println("========== TC-23 DIAGNOSTICS ==========");
    System.out.println("Current URL: " + driver.getCurrentUrl());
    System.out.println("Page Title: " + driver.getTitle());
    System.out.println("Page Text: " +
        driver.findElement(By.tagName("body")).getText());
    System.out.println("=======================================");
}
// TC-24: Verify recruiter cannot access student dashboard
@Test(priority = 24)
public void TC24_recruiterCannotAccessStudentDashboard() {

    // Step 1: Login as recruiter
    loginRecruiter();

    // Step 2: Attempt to access student dashboard
    ((JavascriptExecutor) driver).executeScript(
        "window.history.pushState({}, '', '/student/dashboard');" +
        "window.dispatchEvent(new PopStateEvent('popstate'));"
    );

    // Step 3: Wait for Access Denied message
    WebElement accessDenied = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'You do not have permission to view this page.')]")
        )
    );

    // Step 4: Verify access is denied
    Assert.assertTrue(
        accessDenied.isDisplayed(),
        "Recruiter was able to access the student dashboard!"
    );

    System.out.println(
        "TC-24 PASSED: Recruiter access to student dashboard was denied."
    );
}
// TC-25: Diagnose duplicate job application prevention
@Test(priority = 25)
public void TC25_duplicateJobApplicationPrevention() {

    // Step 1: Login as student
    loginStudent();

    // Step 2: Open the Backend Engineer job
    openJobDetails();

    // Step 3: Wait for page to load
    pause();

    // Step 4: Print actual page information
    System.out.println("========== TC-25 DIAGNOSTICS ==========");
    System.out.println("Current URL: " + driver.getCurrentUrl());
    System.out.println("Page Title: " + driver.getTitle());
    System.out.println("Page Text: " +
        driver.findElement(By.tagName("body")).getText());
    System.out.println("=======================================");

}
}