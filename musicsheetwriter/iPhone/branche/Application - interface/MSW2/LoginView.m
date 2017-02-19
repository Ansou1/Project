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


@implementation LoginView{
    CGRect rect;
}

@synthesize identifiant;
@synthesize password;

- (void)viewDidLoad {
    [super viewDidLoad];
    Id_global = @"0";
    rect = self.view.frame;
    if (![[NSUserDefaults standardUserDefaults] boolForKey:@"HasLaunchedOnce"])
    {
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"HasLaunchedOnce"];
        [[NSUserDefaults standardUserDefaults] setBool:YES forKey:@"deconnection"];
        self.switch1.on = FALSE;
        [[NSUserDefaults standardUserDefaults] synchronize];
    }
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidShow:)
                                                 name:UIKeyboardDidShowNotification
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardDidHide:)
                                                 name:UIKeyboardWillHideNotification
                                               object:nil];
}

- (void)keyboardDidShow: (NSNotification *) notif{
    CGSize keyboardsize = [[[notif userInfo]objectForKey:UIKeyboardFrameBeginUserInfoKey]CGRectValue].size;
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.25];
    long moveScreen = ((long)(keyboardsize.height) - (long)(self.view.frame.size.height) + 465) * -1;
    if (moveScreen > 0)
        moveScreen = 0;
    self.view.frame = CGRectMake(rect.origin.x,moveScreen,rect.size.width,rect.size.height);
    [UIView commitAnimations];
}

- (void)keyboardDidHide: (NSNotification *) notif{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.25];
    self.view.frame = rect;
    [UIView commitAnimations];
}

-(void)viewDidAppear:(BOOL)animated{
    if (![[NSUserDefaults standardUserDefaults] boolForKey:@"deconnection"]) {
        NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
        ApiMethod *api = [[ApiMethod alloc] init];
        NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
        [parametersDictionary setValue:[user objectForKey:@"login"] forKey:@"login"];
        [parametersDictionary setValue:[SSKeychain passwordForService:@"MSW" account:[user objectForKey:@"login"]] forKey:@"password"];
        NSDictionary *dict = [api ApiMethodPostAfnetworkingAt:@"https://musicsheetwriter.tk/api/login" Data:parametersDictionary];
        if (code_global == 200){
            self.id = [dict objectForKey:(@"id")];
            Id_global = self.id;
            [self performSegueWithIdentifier:@"nextView" sender:self];
        }
        else{
            [api popup:dict id:self];
        }
    }
}

- (void)viewWillAppear:(BOOL)animated{
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    
    if ([user objectForKey:@"switch1"] != NULL ){
        if (![[NSUserDefaults standardUserDefaults] boolForKey:@"deconnection"])
            _switch1.on = [user boolForKey:@"switch1"];
        else
            self.switch1.on = FALSE;
    }
    if ([_switch1 isOn] == true){
        identifiant.text = [user objectForKey:@"login"];
        password.text = [SSKeychain passwordForService:@"MSW" account:[user objectForKey:@"login"]];
    }
}

-(IBAction)login{
    ApiMethod *api = [[ApiMethod alloc] init];
    NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
    [parametersDictionary setValue:identifiant.text forKey:@"login"];
    [parametersDictionary setValue:password.text forKey:@"password"];
    NSDictionary *dict = [api ApiMethodPostAfnetworkingAt:@"https://musicsheetwriter.tk/api/login" Data:parametersDictionary];
    if (code_global == 200)
    {
        _label.text = @"Vous êtes connecté.";
        self.id = [dict objectForKey:(@"id")];
        Id_global = self.id;
        if ([_switch1 isOn] == true){
            [[NSUserDefaults standardUserDefaults]setObject:identifiant.text forKey:@"login"];
            [SSKeychain setPassword:password.text forService:@"MSW" account:identifiant.text];
            [[NSUserDefaults standardUserDefaults] setBool:NO forKey:@"deconnection"];
        }
        [[NSUserDefaults standardUserDefaults] setBool:[_switch1 isOn] forKey:@"switch1"];
        
        [self performSegueWithIdentifier:@"nextView" sender:self];
    }
    else{
        [api popup:dict id:self];
    }
}

-(IBAction)Guest{
    [self performSegueWithIdentifier:@"nextView" sender:self];
}

@end
