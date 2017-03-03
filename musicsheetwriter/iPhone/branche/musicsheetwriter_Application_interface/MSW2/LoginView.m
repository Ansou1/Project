//
//  LoginView.m
//  MSW2
//
//  Created by simon on 22/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "LoginView.h"
#import "SSKeychain.h"
#import "SSKeychainQuery.h"


@implementation LoginView

@synthesize identifiant;
@synthesize password;


- (void)viewDidLoad {
    [super viewDidLoad];
    Id_global = @"0";
    }
-(void)viewDidAppear:(BOOL)animated
{
    if ([_switch1 isOn] == true){
        [self login];
    }
}

- (void)viewWillAppear:(BOOL)animated{
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];

    if ([user objectForKey:@"switch1"] != NULL){
        _switch1.on = [user boolForKey:@"switch1"];
    }
    if ([_switch1 isOn] == true){
        identifiant.text = [user objectForKey:@"login"];
        password.text = [SSKeychain passwordForService:@"MSW" account:[user objectForKey:@"login"]];
    }
}

-(IBAction)login{
    ApiMethod *api = [[ApiMethod alloc] init];
    NSDictionary *dict = [api postMethodWithIdentifier:identifiant.text Password:password.text];

    //si error == Null et que id exciste (pas null ?) //
    if (code_global == 200)
    {
        _label.text = @"You are connected.";
        
        self.id = [dict objectForKey:(@"id")];
        Id_global = self.id;

        if ([_switch1 isOn] == true){
        [[NSUserDefaults standardUserDefaults]setObject:identifiant.text forKey:@"login"];
        [SSKeychain setPassword:password.text forService:@"MSW" account:identifiant.text];
        }
        [[NSUserDefaults standardUserDefaults] setBool:[_switch1 isOn] forKey:@"switch1"];
        [self performSegueWithIdentifier:@"nextView" sender:self];
    }
    else
    {
        [api popup:dict];
    }
}

-(IBAction)Guest{
    [self performSegueWithIdentifier:@"nextView" sender:self];
}

@end
