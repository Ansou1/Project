//
//  ProfilViewController.m
//  MSW2
//
//  Created by simon on 15/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "ProfilViewController.h"
#import "SWRevealViewController.h"
#import "AbonneesFromProfilTableViewController.h"
#import "GuestMode.h"

@interface ProfilViewController ()

@end

@implementation ProfilViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    //NSLog(@"%@", _ProfilModal);
    
    _pseudo.text = _ProfilModal[0];
    _idProfil = _ProfilModal[1];
    idProfil_global = _idProfil;
    
    //_barButton.target = self.revealViewController;
    //_barButton.action = @selector(revealToggle:);
    
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api getMethodWithId:(_idProfil)];
    
    if (code_global != 200)
    {
        [api popup:dict1];
        return;
    }
    
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"personal_data"];
    
    if ([dict2 objectForKey:(@"firstname")] != [NSNull null])
        self.name.text = [dict2 objectForKey:(@"firstname")];
    
    if ([dict2 objectForKey:(@"lastname")] != [NSNull null])
        self.surname.text = [dict2 objectForKey:(@"lastname")];
    
    if ([dict2 objectForKey:(@"email")] != [NSNull null])
        self.email.text = [dict2 objectForKey:(@"email")];
    
    if ([dict2 objectForKey:(@"username")] != [NSNull null])
        self.pseudo.text = [dict2 objectForKey:@"username"];
    
    if ([dict2 objectForKey:(@"message")] != [NSNull null])
        self.Message.text = [dict2 objectForKey:@"message"];
    
    // number of subscriptions
    NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions", idProfil_global];
    NSDictionary *dict3 = [api getMethodWithString:post];
    
    if (code_global != 200)
    {
        [api popup:dict3];
        return;
    }
    //NSLog(@"%@", dict3);
    _pseudo2 = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict3) {
        [_pseudo2 addObject:[item valueForKey:@"username"]];
    }
    _abonnements.text = [NSString stringWithFormat:@"%d", _pseudo2.count];
    //------------------------
    // number of subscribers
        post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscribers", idProfil_global];
        NSDictionary *dict4 = [api getMethodWithString:post];
        
        if (code_global != 200)
        {
            [api popup:dict4];
            return;
        }
        _pseudo3 = [[NSMutableArray alloc] initWithCapacity:0];
        for(NSDictionary *item in dict4) {
            [_pseudo3 addObject:[item valueForKey:@"username"]];
        }
    _abonnes.text = [NSString stringWithFormat:@"%d", _pseudo3.count];
    //------------------------
    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
    
    self.profileImageView.layer.cornerRadius = self.profileImageView.frame.size.width / 2;
    self.profileImageView.clipsToBounds = YES;
    self.profileImageView.layer.borderWidth = 3.0f;
    self.profileImageView.layer.borderColor = [UIColor blackColor].CGColor;
    
    UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(actionTapAbonnes:)];
    [self.abonnes setUserInteractionEnabled:YES];
    [self.abonnes addGestureRecognizer:gesture];
    
    UITapGestureRecognizer* gesture2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(actionTapAbonnements:)];
    [self.abonnements setUserInteractionEnabled:YES];
    [self.abonnements addGestureRecognizer:gesture2];
    
    [self refreshStateButton];
}

-(BOOL) checkUserInTheList{
    
    int i = 0;
    
    NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api getMethodWithString:post];
    //NSLog(@"/// %@", dict1);
    if (code_global != 200)
    {
        [api popup:dict1];
        return FALSE;
    }
    
    _pseudoCounter = [[NSMutableArray alloc] initWithCapacity:0];
    _idRowCounter = [[NSMutableArray alloc] initWithCapacity:0];
    
    for(NSDictionary *item in dict1) {
        [_idRowCounter addObject:[item valueForKey:@"id"]];
            }
    while (i < _idRowCounter.count)
    {
        //NSLog(@"%@ ---- %@", _idRowCounter[i], _ProfilModal[1]);
        if ([_idRowCounter[i] isEqualToString:_ProfilModal[1]])
        {
            return TRUE;
        }
        i = i + 1;
    }
    return FALSE;
}

-(void) refreshStateButton{
    
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuest] != true) {
        if (([_star imageForState:UIControlStateNormal] != [UIImage imageNamed:@"star-7 - copie.png"]) && ([self checkUserInTheList]))
        {
            [_star setImage:[UIImage imageNamed:@"star-7 - copie.png"] forState:UIControlStateNormal];
        }
        else{
            [_star setImage:[UIImage imageNamed:@"star-7.png"] forState:UIControlStateNormal];
        }
    }
    else
    {
       [_star setImage:[UIImage imageNamed:@"star-7.png"] forState:UIControlStateNormal];
    }
}

-(void) actionTapAbonnes:(UIGestureRecognizer*)gesture{
    [self performSegueWithIdentifier:@"showAbonnés" sender:self];
}

-(void) actionTapAbonnements:(UIGestureRecognizer*)gesture{
    [self performSegueWithIdentifier:@"showAbonnements" sender:self];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

- (IBAction)star:(id)sender {
    
    if (![self checkUserInTheList]){
       //NSLog(@"\npseudo :: %@\nId :: %@", _ProfilModal[0], _idProfil);
        /*NSString * post =[NSString stringWithFormat:@"{\"id\":\"%@\"}", _idProfil];
        ApiMethod *api = [[ApiMethod alloc]init];
        [api postMethodWithString:post At:@"http://163.5.84.253/api/subscriptions"];*/
        
        NSString * post =[NSString stringWithFormat:@"{\"id\":\"%@\"}", _idProfil];
        NSString * post1 =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions", Id_global];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api postMethodWithString:post At:post1];
       if (code_global != 200)
        {
            [api popup:dict1];
            return;
        }
        [self refreshStateButton];
    }
    else{
        /*NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/subscriptions/%@", _idProfil];
        //NSLog(@"%@", post);
        ApiMethod *api = [[ApiMethod alloc]init];*/
        NSString * post =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/subscriptions/%@",Id_global , _idProfil];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api deleteMethodWithString:post];
        //NSLog(@"%@ /a/a/ %@ /a/a/ %d",post, dict1, code_global);
        if (code_global != 204)
        {
            [api popup:dict1];
            return;
        }
        //NSLog(@"%@",[api deleteMethodWithString:post]);
        [self refreshStateButton];
    }
}
@end

