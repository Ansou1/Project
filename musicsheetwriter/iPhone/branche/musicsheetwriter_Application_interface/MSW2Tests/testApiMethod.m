//
//  ApiMethod.m
//  MSW
//
//  Created by simon on 26/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

//#import <Cocoa/Cocoa.h>
#import <XCTest/XCTest.h>
#import <UIKit/UIKit.h>
#import "ApiMethod.h"


@interface testApiMethod : XCTestCase

@end

@implementation testApiMethod

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
    [self Connection:@"bob" pass:@"b"];
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

-(NSDictionary *)Connection:(NSString *)iden pass:(NSString *)pass{
    NSLog(@"@@@@@@@@@@@@@@@@@@@@@@@@");
    NSLog(@"@Je suis dans connexion@");
    NSLog(@"@@@@@@@@@@@@@@@@@@@@@@@@");
    
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithIdentifier:iden Password:pass];
    //NSLog(@"/////////////////%@", dict);
    if ([dict objectForKey:(@"message")])
        return NULL;
    else
        return dict;
}

- (void)testGetMethodWithId{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de getMethodWithId debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api getMethodWithId:@"53"];
    if(![dict objectForKey:(@"id")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

- (void)test2GetMethodWithId{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de 2getMethodWithId debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api getMethodWithId:@"0"];
    if(![dict objectForKey:(@"message")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodWithIdentifier{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithIdentifier debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithIdentifier:@"bob" Password:@"b"];
    if(![dict objectForKey:(@"id")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)test2PostMethodWithIdentifier{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de 2postMethodWithIdentifier debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithIdentifier:@"bob" Password:@"bbbbbbbb"];
    if(![dict objectForKey:(@"message")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodForSuscribeWithUsername{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithIdentifier debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodForSuscribeWithUsername:@"test" Firstname:@"test" Lastname:@"test" Email:@"test" Password:@"test" Photo:@"test"];
    if([dict objectForKey:(@"message")])
        if (![[dict objectForKey:(@"message")] isEqualToString:@"Username or email already used"])
            XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testPostMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de postMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithString:@"{\"username\":\"bob\",\"password\":\"b\"}" At:@"http://163.5.84.253/msw/web/app.php/api/login"];
    if(![dict objectForKey:(@"id")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)test2PostMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de 2postMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithString:@"{\"username\":\"bob\",\"password\":\"bbbbbbb\"}" At:@"http://163.5.84.253/msw/web/app.php/api/login"];
    if(![dict objectForKey:(@"message")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}

-(void)testGetMethodWithString{
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test de getMethodWithString debute");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api getMethodWithString:@""];
    if(![dict objectForKey:(@"message")])
        XCTAssertTrue(false);
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
    NSLog(@"Le test est fini");
    NSLog(@"--------------------------------------------------------------------------------------------------------------------------");
}
@end
