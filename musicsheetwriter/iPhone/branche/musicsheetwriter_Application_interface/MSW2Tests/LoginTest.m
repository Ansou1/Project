//
//  LoginTest.m
//  MSW2
//
//  Created by simon on 23/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>
#import "LoginView.h"
#import "AccountViewController.h"

@interface LoginTest : XCTestCase

@property (strong, nonatomic) LoginView *controller;

@end

@implementation LoginTest

@synthesize controller = _controller;

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
    /*UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    self.controller = [storyboard instantiateViewControllerWithIdentifier:@"LoginView"];
    [self.controller performSelectorOnMainThread:@selector(loadView) withObject:nil waitUntilDone:YES];*/
    
    //pour toujours etre connecté au moins une fois
    [self Connexion:@"bob" pass:@"b"];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

-(void)ConnexionWithButton{
    
    //UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    
    LoginView *log = [storyboard instantiateViewControllerWithIdentifier:@"LoginView"];
    [UIApplication sharedApplication].keyWindow.rootViewController = log;
    
    //[log performSegueWithIdentifier:@"nextView" sender:nil];
    
    //XCTAssertNotNil(log.presentedViewController);
    
    //LoginView *log = [[LoginView alloc] init];
    UITextField *text1 = [[UITextField alloc] init];
    text1.text = @"lol2";
    UITextField *text2 = [[UITextField alloc] init];
    text2.text = @"qwerty123";
    log.identifiant = text1;
    log.password = text2;
    
    [log login];
    //[log loadView];
}

-(NSDictionary *)Connexion:(NSString *)iden pass:(NSString *)pass{
    NSLog(@"@@@@@@@@@@@@@@@@@@@@@@@@");
    NSLog(@"@Je suis dans connexion@");
    NSLog(@"@@@@@@@@@@@@@@@@@@@@@@@@");
    
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithIdentifier:iden Password:pass];
    NSLog(@"/////////////////%d", code_global);
    //if (code_global != 200)
    //    return NULL;
    //else
        return dict;
}

//-------------------------------------------------------------------------------------------------------------------------
// test de l'ecran de connexion - login
//-------------------------------------------------------------------------------------------------------------------------
-(void)testConnection{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de log debute. connexion avec lol2 - qwerty123");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    
    NSDictionary *dict = [self Connexion:@"lol2" pass:@"qwerty123"];
    XCTAssertNotNil(dict, @"l'objet retourné a la connexion est NULL, ce n'est pas normal. test: log (lol2 - qwerty123)");
    
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testConnectionFail{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de log debute. connexion avec lol2 - mauvais mot de passe");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    
    NSDictionary *dict = [self Connexion:@"lol2" pass:@"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb123456789azertyyuiopmlkjhgfds"];
    XCTAssertNil(dict, @"l'objet retourné a la connexion n'est NULL, ce n'est pas normal. test: log (bob - mot de passe mauvais)");
    
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

//-------------------------------------------------------------------------------------------------------------------------
// test de l'ecran de connexion - creation de compte
//-------------------------------------------------------------------------------------------------------------------------

//-------------------------------------------------------------------------------------------------------------------------
// test de l'api
//-------------------------------------------------------------------------------------------------------------------------
-(void)testGetMethodWithId{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de getMethodWithId debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    XCTAssertNotNil([api getMethodWithId:@"55"]);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodWithIdentifier{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithIdentifier debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    //NSLog(@"111111111%@",[api postMethodWithIdentifier:@"bob" Password:@"b"]);
    XCTAssertNotNil([api postMethodWithIdentifier:@"lol2" Password:@"qwerty123"]);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodForSuscribeWithUsername{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithIdentifier debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    //NSLog(@"111111111%@",[api postMethodWithIdentifier:@"bob" Password:@"b"]);
    XCTAssertNotNil([api postMethodForSuscribeWithUsername:@"test" Firstname:@"test" Lastname:@"test" Email:@"test" Password:@"test" Photo:@"test"]);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    //NSLog(@"111111111%@",[api postMethodWithString:@"{\"username\":\"bob\",\"password\":\"b\"}" At:@"http://163.5.84.253/msw/web/app.php/api/login"]);
    XCTAssertNotNil([api postMethodWithString:@"{\"username\":\"lol2\",\"password\":\"qwerty123\"}" At:@"http://163.5.84.253/api/login"]);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testGetMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de getMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    //NSLog(@"111111111%@",[api getMethodWithString:@"http://163.5.84.253/msw/web/app.php/api/users"]);
    XCTAssertNotNil([api getMethodWithString:@"http://163.5.84.253/api/users"]);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPutMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de putMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");

    //[self ConnexionWithButton];
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api putMethodWithString:@"{\"firstname\":\"lol123456\"}" At:@"http://163.5.84.253/api/users"];
    XCTAssertNotNil(dict);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testOptionsMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de optionsMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    
    //[self ConnexionWithButton];
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api optionsMethodWithString:@"{\"firstname\":\"lol123456\"}" At:@"http://163.5.84.253/api/users"];
    XCTAssertNotNil(dict);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}
/* ce pouvoir a ete scellé car trop dangereux pour les mortels !!! ne liberez pas le monstre qui s'y trouve !!!
-(void)testDeleteMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de deleteMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    
    //[self ConnexionWithButton];
    [self Connexion:@"bob" pass:@"b"];
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api deleteMethodWithString:@"http://163.5.84.253/msw/web/app.php/api"];
    XCTAssertNotNil(dict);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}*/

@end
